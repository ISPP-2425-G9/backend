package com.caronte.caronte.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.RegisterRequestCompany;
import com.caronte.caronte.auth.payload.response.RegisterRequestCustomer;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyRepository;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserRepository;

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
    }

    static Stream<RegisterRequestCustomer> provideValidCustomerRequests() {
        return Stream.of(
            new RegisterRequestCustomer("test1@example.com", "password", "password", "12345678A"),
            new RegisterRequestCustomer("test2@example.com", "securePass123", "securePass123", "87654321B")
        );
    }

    static Stream<RegisterRequestCompany> provideValidCompanyRequests() {
        return Stream.of(
            new RegisterRequestCompany("company1@example.com", "password", "password", "B12345678"),
            new RegisterRequestCompany("company2@example.com", "securePass456", "securePass456", "C87654321")
        );
    }

    // @ParameterizedTest
    // @MethodSource("provideValidCustomerRequests")
    // void validateAndBuildCustomer_ValidData_ReturnsCustomer(RegisterRequestCustomer request) {
    //     ErrorHandler errors = new ErrorHandler();

    //     when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
    //     when(customerRepository.existsByDni(request.getDni())).thenReturn(false);
    //     when(passwordEncoder.encode(request.getPassword1())).thenReturn("encodedPassword");
    //     when(request.parse(passwordEncoder)).thenReturn(mockCustomer);

    //     Customer customer = authService.validateAndBuildCustomer(request, errors);

    //     assertNotNull(customer);
    //     assertTrue(errors.getErrors().isEmpty());
    // }

    // @ParameterizedTest
    // @MethodSource("provideValidCompanyRequests")
    // void validateAndBuildCompany_ValidData_ReturnsCompany(RegisterRequestCompany request) {
    //     ErrorHandler errors = new ErrorHandler();

    //     when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
    //     when(companyRepository.existsByNif(request.getNif())).thenReturn(false);
    //     when(passwordEncoder.encode(request.getPassword1())).thenReturn("encodedPassword");
    //     when(request.parse(passwordEncoder)).thenReturn(mockCompany);

    //     Company company = authService.validateAndBuildCompany(request, errors);

    //     assertNotNull(company);
    //     assertTrue(errors.getErrors().isEmpty());
    // }

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