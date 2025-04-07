package com.caronte.caronte.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.caronte.caronte.auth.payload.response.RegisterRequestCompany;
import com.caronte.caronte.auth.payload.response.RegisterRequestCustomer;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyRepository;
import com.caronte.caronte.company.CompanyType;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserRepository;
import com.caronte.caronte.util.ErrorHandler;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Mock
    private RegisterRequestCustomer mockCustomerRequest;

    @Mock
    private RegisterRequestCompany mockCompanyRequest;

    @Mock
    private Customer mockCustomer;

    @Mock
    private Company mockCompany;

    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurar mocks de CustomerRequest
        when(mockCustomerRequest.getName()).thenReturn("John Doe");
        when(mockCustomerRequest.getEmail()).thenReturn("john.doe@example.com");
        when(mockCustomerRequest.getPassword1()).thenReturn("password123");
        when(mockCustomerRequest.getPassword2()).thenReturn("password123");
        when(mockCustomerRequest.getTelephone()).thenReturn("666777888");
        when(mockCustomerRequest.getDni()).thenReturn("12345678Z");

        // Configurar mocks de CompanyRequest
        when(mockCompanyRequest.getName()).thenReturn("Company 1");
        when(mockCompanyRequest.getEmail()).thenReturn("company@gmail.com");
        when(mockCompanyRequest.getPassword1()).thenReturn("1234");
        when(mockCompanyRequest.getPassword2()).thenReturn("1234");
        when(mockCompanyRequest.getTelephone()).thenReturn("123123123");
        when(mockCompanyRequest.getAddress()).thenReturn("Av. Reina Mercedes");
        when(mockCompanyRequest.getCity()).thenReturn("Sevilla");
        when(mockCompanyRequest.getZipCode()).thenReturn("12345");
        when(mockCompanyRequest.getNif()).thenReturn("A12345678");
        when(mockCompanyRequest.getImageUrl()).thenReturn("http://company.png");
        when(mockCompanyRequest.getDescription()).thenReturn("Descripcion company");
        when(mockCompanyRequest.getCompanyType()).thenReturn(CompanyType.FUNERARIA);
    }

    @Test
    void validateAndBuildCustomer_ValidData_ReturnsCustomer() {
        BindingResult bindingResult = new BeanPropertyBindingResult(mockCustomerRequest, "mockCustomerRequest");
        ErrorHandler errors = ErrorHandler.catchError(bindingResult);

        when(userRepository.existsByEmail(mockCustomerRequest.getEmail())).thenReturn(false);
        when(customerRepository.existsByDni(mockCustomerRequest.getDni())).thenReturn(false);
        when(passwordEncoder.encode(mockCustomerRequest.getPassword1())).thenReturn("encodedPassword");
        when(mockCustomerRequest.parse(passwordEncoder)).thenReturn(mockCustomer);

        Customer customer = authService.validateAndBuildCustomer(mockCustomerRequest, errors);

        assertNotNull(customer);
        assertTrue(errors.getErrors().isEmpty());
    }

    @Test
    void validateAndBuildCompany_ValidData_ReturnsCompany() {
        BindingResult bindingResult = new BeanPropertyBindingResult(mockCompanyRequest, "mockCompanyRequest");
        ErrorHandler errors = ErrorHandler.catchError(bindingResult);

        when(userRepository.existsByEmail(mockCompanyRequest.getEmail())).thenReturn(false);
        when(companyRepository.existsByNif(mockCompanyRequest.getNif())).thenReturn(false);
        when(passwordEncoder.encode(mockCompanyRequest.getPassword1())).thenReturn("encodedPassword");
        when(mockCompanyRequest.parse(passwordEncoder)).thenReturn(mockCompany);

        Company company = authService.validateAndBuildCompany(mockCompanyRequest, errors);

        assertNotNull(company);
        assertTrue(errors.getErrors().isEmpty());
    }

    @Test
    @Transactional
    void save_ValidUser_SavesSuccessfully() {
        authService.save(mockUser);

        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void getNameById_ValidUserId_ReturnsName() {
        Long userId = 1L;
        when(mockUser.getName()).thenReturn("John Doe");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        String name = authService.getNameById(userId);

        assertEquals("John Doe", name);
    }
}
