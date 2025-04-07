package com.caronte.caronte.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.caronte.caronte.auth.payload.response.RegisterRequestCompany;
import com.caronte.caronte.auth.payload.response.RegisterRequestCustomer;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyRepository;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserRepository;
import com.caronte.caronte.util.ErrorHandler;

public class AuthServiceTest {

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
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testValidateAndBuildCustomer_Success() {
        RegisterRequestCustomer request = spy(new RegisterRequestCustomer());
        request.setEmail("test@example.com");
        request.setPassword1("password");
        request.setPassword2("password");
        request.setDni("12345678X");
        
        ErrorHandler errors = new ErrorHandler();
        ReflectionTestUtils.setField(errors, "errors", new HashMap<String, List<String>>());
        
        Customer dummyCustomer = new Customer();
        doReturn(dummyCustomer).when(request).parse(passwordEncoder);
        
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(customerRepository.existsByDni("12345678X")).thenReturn(false);
        
        Customer result = authService.validateAndBuildCustomer(request, errors);
        
        Map<String, List<String>> errorMap = errors.getErrors();
        assertNotNull(errorMap);
        assertTrue(errorMap.isEmpty());
        assertSame(dummyCustomer, result);
    }
    
    @Test
    void testValidateAndBuildCustomer_PasswordMismatch() {
        RegisterRequestCustomer request = spy(new RegisterRequestCustomer());
        request.setEmail("test@example.com");
        request.setPassword1("password");
        request.setPassword2("different");
        request.setDni("12345678X");
        
        ErrorHandler errors = new ErrorHandler();
        ReflectionTestUtils.setField(errors, "errors", new HashMap<String, List<String>>());
        
        Customer dummyCustomer = new Customer();
        doReturn(dummyCustomer).when(request).parse(passwordEncoder);
        
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(customerRepository.existsByDni("12345678X")).thenReturn(false);
        
        Customer result = authService.validateAndBuildCustomer(request, errors);
        
        Map<String, List<String>> errorMap = errors.getErrors();
        assertTrue(errorMap.containsKey("password"));
        assertEquals("Las contraseñas no coinciden", errorMap.get("password").get(0));
        assertSame(dummyCustomer, result);
    }
    
    @Test
    void testValidateAndBuildCustomer_EmailExists() {
        RegisterRequestCustomer request = spy(new RegisterRequestCustomer());
        request.setEmail("test@example.com");
        request.setPassword1("password");
        request.setPassword2("password");
        request.setDni("12345678X");
        
        ErrorHandler errors = new ErrorHandler();
        ReflectionTestUtils.setField(errors, "errors", new HashMap<String, List<String>>());
        
        Customer dummyCustomer = new Customer();
        doReturn(dummyCustomer).when(request).parse(passwordEncoder);
        
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        when(customerRepository.existsByDni("12345678X")).thenReturn(false);
        
        Customer result = authService.validateAndBuildCustomer(request, errors);
        
        Map<String, List<String>> errorMap = errors.getErrors();
        assertTrue(errorMap.containsKey("email"));
        assertEquals("El email ya está en uso", errorMap.get("email").get(0));
        assertSame(dummyCustomer, result);
    }
    
    @Test
    void testValidateAndBuildCustomer_DniExists() {
        RegisterRequestCustomer request = spy(new RegisterRequestCustomer());
        request.setEmail("test@example.com");
        request.setPassword1("password");
        request.setPassword2("password");
        request.setDni("12345678X");
        
        ErrorHandler errors = new ErrorHandler();
        ReflectionTestUtils.setField(errors, "errors", new HashMap<String, List<String>>());
        
        Customer dummyCustomer = new Customer();
        doReturn(dummyCustomer).when(request).parse(passwordEncoder);
        
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(customerRepository.existsByDni("12345678X")).thenReturn(true);
        
        Customer result = authService.validateAndBuildCustomer(request, errors);
        
        Map<String, List<String>> errorMap = errors.getErrors();
        assertTrue(errorMap.containsKey("dni"));
        assertEquals("El dni ya está en uso", errorMap.get("dni").get(0));
        assertSame(dummyCustomer, result);
    }
    

    @Test
    void testValidateAndBuildCompany_Success() {
        RegisterRequestCompany request = spy(new RegisterRequestCompany());
        request.setEmail("company@example.com");
        request.setPassword1("password");
        request.setPassword2("password");
        request.setNif("NIF123");
        
        ErrorHandler errors = new ErrorHandler();
        ReflectionTestUtils.setField(errors, "errors", new HashMap<String, List<String>>());
        
        Company dummyCompany = new Company();
        doReturn(dummyCompany).when(request).parse(passwordEncoder);
        
        when(userRepository.existsByEmail("company@example.com")).thenReturn(false);
        when(companyRepository.existsByNif("NIF123")).thenReturn(false);
        
        Company result = authService.validateAndBuildCompany(request, errors);
        
        Map<String, List<String>> errorMap = errors.getErrors();
        assertNotNull(errorMap);
        assertTrue(errorMap.isEmpty());
        assertSame(dummyCompany, result);
    }
    
    @Test
    void testValidateAndBuildCompany_PasswordMismatch() {
        RegisterRequestCompany request = spy(new RegisterRequestCompany());
        request.setEmail("company@example.com");
        request.setPassword1("password");
        request.setPassword2("different");
        request.setNif("NIF123");
        
        ErrorHandler errors = new ErrorHandler();
        ReflectionTestUtils.setField(errors, "errors", new HashMap<String, List<String>>());
        
        Company dummyCompany = new Company();
        doReturn(dummyCompany).when(request).parse(passwordEncoder);
        
        when(userRepository.existsByEmail("company@example.com")).thenReturn(false);
        when(companyRepository.existsByNif("NIF123")).thenReturn(false);
        
        Company result = authService.validateAndBuildCompany(request, errors);
        
        Map<String, List<String>> errorMap = errors.getErrors();
        assertTrue(errorMap.containsKey("password"));
        assertEquals("Las contraseñas no coinciden", errorMap.get("password").get(0));
        assertSame(dummyCompany, result);
    }
    
    @Test
    void testValidateAndBuildCompany_EmailExists() {
        RegisterRequestCompany request = spy(new RegisterRequestCompany());
        request.setEmail("company@example.com");
        request.setPassword1("password");
        request.setPassword2("password");
        request.setNif("NIF123");
        
        ErrorHandler errors = new ErrorHandler();
        ReflectionTestUtils.setField(errors, "errors", new HashMap<String, List<String>>());
        
        Company dummyCompany = new Company();
        doReturn(dummyCompany).when(request).parse(passwordEncoder);
        
        when(userRepository.existsByEmail("company@example.com")).thenReturn(true);
        when(companyRepository.existsByNif("NIF123")).thenReturn(false);
        
        Company result = authService.validateAndBuildCompany(request, errors);
        
        Map<String, List<String>> errorMap = errors.getErrors();
        assertTrue(errorMap.containsKey("email"));
        assertEquals("El email ya está en uso", errorMap.get("email").get(0));
        assertSame(dummyCompany, result);
    }
    
    @Test
    void testValidateAndBuildCompany_NifExists() {
        RegisterRequestCompany request = spy(new RegisterRequestCompany());
        request.setEmail("company@example.com");
        request.setPassword1("password");
        request.setPassword2("password");
        request.setNif("NIF123");
        
        ErrorHandler errors = new ErrorHandler();
        ReflectionTestUtils.setField(errors, "errors", new HashMap<String, List<String>>());
        
        Company dummyCompany = new Company();
        doReturn(dummyCompany).when(request).parse(passwordEncoder);
        
        when(userRepository.existsByEmail("company@example.com")).thenReturn(false);
        when(companyRepository.existsByNif("NIF123")).thenReturn(true);
        
        Company result = authService.validateAndBuildCompany(request, errors);
        
        Map<String, List<String>> errorMap = errors.getErrors();
        assertTrue(errorMap.containsKey("nif"));
        assertEquals("El NIF ya está en uso", errorMap.get("nif").get(0));
        assertSame(dummyCompany, result);
    }
    

    @Test
    void testSave() {
        User user = new User();
        authService.save(user);
        verify(userRepository).save(user);
    }
    
    @Test
    void testGetNameById_Found() {
        User user = new User();
        user.setName("Test User");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        String name = authService.getNameById(1L);
        assertEquals("Test User", name);
    }
    
    @Test
    void testGetNameById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> authService.getNameById(1L));
        assertEquals("Usuario no encontrado", exception.getMessage());
    }
}
