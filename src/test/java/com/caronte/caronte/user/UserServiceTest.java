package com.caronte.caronte.user;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.caronte.caronte.auth.payload.response.UserChangePasswordRequest;
import com.caronte.caronte.configuration.authorization.Authorization;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.util.exceptions.ResourceNotFound;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDetailsImpl userDetails;

    private Collection<? extends GrantedAuthority> adminAuthorities = List.of(() -> "ROLE_ADMIN");
    private Collection<? extends GrantedAuthority> userAuthorities = List.of(() -> "ROLE_USER");

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        userDetails = new UserDetailsImpl(1L, "test", "password", userAuthorities);

        SecurityContextHolder.clearContext();
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User found = userService.findById(1L);
        assertEquals(1L, found.getId());
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class, () -> userService.findById(1L));
    }

    @Test
    void findByEmail_shouldReturnUserOptional() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        Optional<User> result = userService.findByEmail("test@example.com");
        assertTrue(result.isPresent());
    }

    @Test
    void findCurrentUser_shouldReturnAuthenticatedUser() {
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.findCurrentUser();
        assertEquals(1L, result.getId());
    }

    @Test
    void findCurrentUser_shouldThrow_whenUnauthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThrows(Exception.class, () -> userService.findCurrentUser());
    }

    @Test
    void findCurrentUserId_shouldReturnId() {
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Long id = userService.findCurrentUserId();
        assertEquals(1L, id);
    }

    @Test
    void findCurrentUserEmail_shouldReturnEmail() {
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        String email = userService.findCurrentUserEmail();
        assertEquals("test@example.com", email);
    }

    @Test
    void authorizeUserOrAdmin_shouldAllowSameUser() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.authorizeUserOrAdmin(1L, "error");
        assertEquals(1L, result.getId());
    }


    @Test
    void authorizeUserOrAdmin_shouldAllowAdmin() {
        UserDetailsImpl adminDetails = new UserDetailsImpl(2L, "admin", "pass", adminAuthorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(adminDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        User result = userService.authorizeUserOrAdmin(1L, "error");
        assertEquals(user.getId(), result.getId());
    }


    @Test
    void authorizeUserOrAdmin_shouldThrow_whenNotAuthorized() {
        UserDetailsImpl anotherUser = new UserDetailsImpl(2L, "other", "pass", userAuthorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(anotherUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // ID diferente al actual (2L vs 99L)
        assertThrows(Exception.class, () -> userService.authorizeUserOrAdmin(99L, "not allowed"));
    }
    
    

    @Test
    void authorizeUser_shouldAllowSelf() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.authorizeUser(1L, "nope");
        assertEquals(1L, result.getId());
    }
    
    @Test
    void authorizeUser_shouldThrow_whenDifferentUser() {
        UserDetailsImpl other = new UserDetailsImpl(2L, "no", "x", userAuthorities);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(other);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThrows(Exception.class, () -> userService.authorizeUser(99L, "not your user"));
    }
    
    

    @Test
    void delete_shouldCallRepositoryDeleteById() {
        userService.delete(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void changePassword_shouldUpdateAndSavePassword() {
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setNewPassword("newPass");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(user);
        User result = userService.changePassword(1L, request);
        assertEquals(user, result);
    }

    @Test
    void changePassword_shouldThrow_whenUserNotFound() {
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class, () -> userService.changePassword(1L, request));
    }

    @Test
    void authorizeAdmin_shouldPassForAdmin() {
        UserDetailsImpl admin = new UserDetailsImpl(
                1L,
                "admin",
                "x",
                List.of(Authorization.ADMIN.getAuthority())
        );
        when(authentication.getPrincipal()).thenReturn(admin);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertDoesNotThrow(() -> userService.authorizeAdmin("forbidden"));
    }

    

    @Test
    void authorizeAdmin_shouldThrow_whenNotAdmin() {
        UserDetailsImpl notAdmin = new UserDetailsImpl(1L, "user", "x", userAuthorities);
        when(authentication.getPrincipal()).thenReturn(notAdmin);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThrows(Exception.class, () -> userService.authorizeAdmin("forbidden"));
    }
}
