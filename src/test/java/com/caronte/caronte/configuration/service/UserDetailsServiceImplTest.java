package com.caronte.caronte.configuration.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.caronte.caronte.admin.Admin;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyRepository;
import com.caronte.caronte.configuration.services.UserDetailsServiceImpl;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.plan.Plan;
import com.caronte.caronte.plan.PlanType;
import com.caronte.caronte.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private CompanyRepository companyRepository;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new UserDetailsServiceImpl(userRepository, customerRepository, companyRepository);
    }

    @Test
    void testLoadUserByUsername_withEmail_found() {
        // Usamos una instancia de Admin en lugar de un User genérico
        String email = "test@example.com";
        Admin dummyAdmin = mock(Admin.class);
        when(dummyAdmin.getId()).thenReturn(1L);
        when(dummyAdmin.getEmail()).thenReturn(email);
        when(dummyAdmin.getPassword()).thenReturn("password");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(dummyAdmin));

        // Act
        UserDetails details = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(details);
        assertEquals(email, details.getUsername());
    }

    @Test
    void testLoadUserByUsername_withEmail_notFound() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email));
        assertEquals("User Not Found with email: " + email, exception.getMessage());
    }

    @Test
void testLoadUserByUsername_withDni_found() {
    String dni = "12345678X";
    Customer dummyCustomer = mock(Customer.class);
    when(dummyCustomer.getId()).thenReturn(2L);
    when(dummyCustomer.getEmail()).thenReturn("dummy@customer.com");
    when(dummyCustomer.getPassword()).thenReturn("custpass");
    
    // Usamos un stub único para getPlan():
    Plan mockPlan = mock(Plan.class);
    when(mockPlan.getPlanType()).thenReturn(PlanType.FREE);
    when(dummyCustomer.getPlan()).thenReturn(mockPlan);

    when(customerRepository.findByDni(dni)).thenReturn(Optional.of(dummyCustomer));

    // Act
    UserDetails details = userDetailsService.loadUserByUsername(dni);

    // Assert
    assertNotNull(details);
    assertEquals("dummy@customer.com", details.getUsername());
}

    @Test
    void testLoadUserByUsername_withDni_notFound() {
        String dni = "12345678X";
        when(customerRepository.findByDni(dni)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(dni));
        assertEquals("User Not Found with DNI: " + dni, exception.getMessage());
    }

    @Test
    void testLoadUserByUsername_withNif_found() {
        String nif = "A1234567B";
        Company dummyCompany = mock(Company.class);
        when(dummyCompany.getId()).thenReturn(3L);
        when(dummyCompany.getEmail()).thenReturn("dummy@company.com");
        when(dummyCompany.getPassword()).thenReturn("comppass");
    
        // Usamos un stub único para getPlan():
        Plan mockPlan = mock(Plan.class);
        when(mockPlan.getPlanType()).thenReturn(PlanType.FREE);
        when(dummyCompany.getPlan()).thenReturn(mockPlan);
    
        when(companyRepository.findByNif(nif)).thenReturn(Optional.of(dummyCompany));
    
        // Act
        UserDetails details = userDetailsService.loadUserByUsername(nif);
    
        // Assert
        assertNotNull(details);
        assertEquals("dummy@company.com", details.getUsername());
    }

    @Test
    void testLoadUserByUsername_withNif_notFound() {
        String nif = "A1234567B";
        when(companyRepository.findByNif(nif)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(nif));
        assertEquals("User Not Found with NIF: " + nif, exception.getMessage());
    }

    @Test
    void testLoadUserByUsername_withInvalidPattern() {
        // Arr: Se pasa un valor que no cumple con ninguno de los patrones (email, DNI o NIF).
        String invalidUsername = "invalid_username";
        // Por la implementación se retorna null
        UserDetails details = userDetailsService.loadUserByUsername(invalidUsername);
        assertNull(details);
    }
}
