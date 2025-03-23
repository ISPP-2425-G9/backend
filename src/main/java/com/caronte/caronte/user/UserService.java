package com.caronte.caronte.user;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.auth.payload.response.UserChangePasswordRequest;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.util.exceptions.ResponseThrow;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public User findCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        return userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional(readOnly = true)
    public User authorizeUserOrAdmin(Long userId, String message){
        User user = findCurrentUser();
        UserDetailsImpl auth =  (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ResponseThrow.checkOrBadRequest(user.getId() == userId || auth.isAdmin(), message);
        return user;
    }

    @Transactional(readOnly = true)
    public User authorizeUserOrAdmin(Long id){
        return authorizeUserOrAdmin(id, "No puedes realizar acciones en la cuenta de otro usuario");
    }


    @Transactional(readOnly = true)
    public User authorizeUser(Long userId, String message){
        User user = findCurrentUser();
        ResponseThrow.checkOrBadRequest(user.getId() == userId, message);
        return user;
    }

    @Transactional(readOnly = true)
    public User authorizeUser(Long id){
        return authorizeUserOrAdmin(id, "No puedes realizar acciones en la cuenta de otro usuario");
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User changePassword(Long id, UserChangePasswordRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setPassword(this.passwordEncoder.encode(request.getNewPassword()));
        return userRepository.save(user);
    }

}
