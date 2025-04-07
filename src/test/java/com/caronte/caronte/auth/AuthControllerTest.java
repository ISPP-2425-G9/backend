package com.caronte.caronte.auth;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.caronte.caronte.admin.Admin;
import com.caronte.caronte.auth.payload.response.CompanyUpdateRequest;
import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;
import com.caronte.caronte.auth.payload.response.LoginRequest;
import com.caronte.caronte.auth.payload.response.RegisterRequestCompany;
import com.caronte.caronte.auth.payload.response.RegisterRequestCustomer;
import com.caronte.caronte.auth.payload.response.UserChangePasswordRequest;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyRepository;
import com.caronte.caronte.company.CompanyService;
import com.caronte.caronte.company.CompanyType;
import com.caronte.caronte.configuration.jwt.JwtUtils;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerService;
import com.caronte.caronte.plan.Plan;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserService;
import com.caronte.caronte.util.exceptions.ErrorHandlerException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserService userService;

    @Mock
    private CustomerService customerService;

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private AuthController authController;

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }
    
    @Test
    void testAuthenticateUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setId("user@example.com");
        loginRequest.setPassword("password");
    
        Customer user = new Customer();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("Test User");
        user.setPassword("encodedPassword");
        user.setIsActive(true);
        user.setDni("12345678X");
        
        user.setPlan(Plan.newPlanFree());
    
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
    
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
               .thenReturn(authentication);
        Mockito.when(userService.findById(1L)).thenReturn(user);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(jwtUtils.generateJwtToken(Mockito.any(Authentication.class))).thenReturn("dummy-jwt");
        Mockito.when(authService.getNameById(1L)).thenReturn("Test User");
    
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-jwt"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("user@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void testAuthenticateUserValidationError() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setId("");
        loginRequest.setPassword("password");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(loginRequest);

        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andReturn();
        });

        Throwable rootCause = exception;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        assertTrue(rootCause instanceof ErrorHandlerException, "The root cause must be ErrorHandlerException");
    }


    @Test
    void testRegisterCustomer() throws Exception {
        RegisterRequestCustomer request = new RegisterRequestCustomer();
        request.setName("Customer A");
        request.setEmail("customerA@example.com");
        request.setPassword1("password");
        request.setPassword2("password");
        request.setTelephone("987654321");
        request.setDni("12345678X");

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Customer A");
        customer.setEmail("customerA@example.com");
        customer.setDni("12345678X");
        customer.setIsActive(true);
        customer.setPlan(Plan.newPlanFree());

        Mockito.when(authService.validateAndBuildCustomer(Mockito.any(RegisterRequestCustomer.class), Mockito.any()))
            .thenReturn(customer);

        UserDetailsImpl userDetails = UserDetailsImpl.build(customer);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        Mockito.when(userService.findById(1L)).thenReturn(customer);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(jwtUtils.generateJwtToken(Mockito.any(Authentication.class))).thenReturn("dummy-jwt");
        Mockito.when(authService.getNameById(1L)).thenReturn("Customer A");

        mockMvc.perform(post("/api/auth/customers/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("dummy-jwt"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("customerA@example.com"))
            .andExpect(jsonPath("$.name").value("Customer A"));
    }


    @Test
    public void testRegisterCustomerValidationError() throws Exception {
        RegisterRequestCustomer request = new RegisterRequestCustomer();
        request.setName("");
        request.setEmail("invalid-email");      
        request.setPassword1("pass123");
        request.setPassword2("pass123");
        request.setTelephone("");               
        request.setDni("1234");                  
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);
    
        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/api/auth/customers/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andReturn();
        });
    
        Throwable rootCause = exception;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        assertTrue(rootCause instanceof ErrorHandlerException,
            "The root cause must be ErrorHandlerException");
    }

    @Test
    void testRegisterCompany() throws Exception {
        RegisterRequestCompany request = new RegisterRequestCompany();
        request.setName("Company A");
        request.setEmail("companya@example.com");
        request.setPassword1("password");
        request.setPassword2("password");
        request.setTelephone("123456789");
        request.setAddress("Street 123");
        request.setCity("City");
        request.setZipCode("12345");
        request.setNif("A1234567B");
        request.setCompanyType(CompanyType.OTRO);

        Company company = new Company();
        company.setId(1L);
        company.setName("Company A");
        company.setEmail("companya@example.com");
        company.setAddress("Street 123");
        company.setCity("City");
        company.setZipCode("12345");
        company.setNif("A2345678Z");
        company.setPlan(Plan.newPlanFree());
        company.setCompanyType(CompanyType.OTRO);

        Mockito.when(authService.validateAndBuildCompany(Mockito.any(RegisterRequestCompany.class), Mockito.any()))
            .thenReturn(company);

        UserDetailsImpl userDetails = UserDetailsImpl.build(company);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        Mockito.when(userService.findById(1L)).thenReturn(company);    
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(jwtUtils.generateJwtToken(Mockito.any(Authentication.class))).thenReturn("dummy-jwt");
        Mockito.when(authService.getNameById(1L)).thenReturn("Company A");

        mockMvc.perform(post("/api/auth/companies/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("dummy-jwt"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("companya@example.com"))
            .andExpect(jsonPath("$.name").value("Company A"));
    }


    @Test
    public void testRegisterCompanyValidationError() throws Exception {
        RegisterRequestCompany request = new RegisterRequestCompany();
        request.setName("");
        request.setEmail("invalid-email");
        request.setPassword1("");
        request.setPassword2("");
        request.setTelephone("");
        request.setAddress("");
        request.setCity("");
        request.setZipCode("1234");
        request.setNif("1234");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/api/auth/companies/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andReturn();
        });

        Throwable rootCause = exception;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        assertTrue(rootCause instanceof ErrorHandlerException);
        ErrorHandlerException ehe = (ErrorHandlerException) rootCause;
        Map<String, List<String>> errors = ehe.getErrorHandler().getErrors();
        assertEquals("El nombre es requerido", errors.get("name").get(0));
        assertEquals("Invalid email format", errors.get("email").get(0));
        assertEquals("Password1 es requerido", errors.get("password1").get(0));
        assertEquals("Password2 es requerido", errors.get("password2").get(0));
        assertEquals("El teléfono es requerido", errors.get("telephone").get(0));
        assertEquals("La dirección es requerida", errors.get("address").get(0));
        assertEquals("La ciudad es requerida", errors.get("city").get(0));
        assertEquals("Formato de código postal invalido", errors.get("zipCode").get(0));
        assertEquals("Formato de NIF invalido", errors.get("nif").get(0));
    }

    @Test
    void testGetCustomer() throws Exception {
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("Customer A");
        customer.setEmail("customerA@example.com");
        
        User customerUser = new User();
        customerUser.setId(customerId);
        customerUser.setEmail("customerA@example.com");
        customerUser.setName("Customer A");
        
        Mockito.when(userService.findCurrentUser()).thenReturn(customerUser);
        Mockito.when(customerService.findById(customerId)).thenReturn(customer);
        
        mockMvc.perform(get("/api/auth/customers/{customerId}", customerId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Customer A"));
    }

    @Test
    void testGetCustomerNotFound() throws Exception {
        Long customerId = 1L;
        when(userService.authorizeUserOrAdmin(Mockito.anyLong(), Mockito.anyString())).thenReturn(null);
        when(customerService.findById(customerId)).thenThrow(new RuntimeException("Customer not found"));
        
        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/api/auth/customers/{customerId}", customerId))
                .andReturn();
        });
        
        Throwable rootCause = exception;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        
        assertTrue(rootCause instanceof RuntimeException);
        assertEquals("Customer not found", rootCause.getMessage());
    }



    @Test
    void testUpdateCustomer() throws Exception {
        Long customerId = 1L;
        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setEmail("new@example.com");
        request.setFullName("New Name");
        request.setTelephone("123456789");
    
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setEmail("new@example.com");
        customer.setName("New Name");
        customer.setTelephone("123456789");
        customer.setIsActive(true);
        customer.setDni("12345678X");
        customer.setPlan(Plan.newPlanFree());

    
        when(userService.findByEmail("new@example.com")).thenReturn(Optional.of(customer));
        when(customerService.update(customerId, request)).thenReturn(customer);
        when(jwtUtils.generateJwtToken(Mockito.any(UserDetailsImpl.class))).thenReturn("dummy-jwt");
        when(userService.findCurrentUser()).thenReturn(customer);
    
        mockMvc.perform(put("/api/auth/customers/{customerId}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("dummy-jwt"))
            .andExpect(jsonPath("$.id").value(customerId))
            .andExpect(jsonPath("$.username").value("new@example.com"))
            .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void testUpdateCustomerEmailMismatch() throws Exception {
        Long customerId = 1L;
        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setEmail("different@example.com");
        request.setFullName("New Name");
        request.setTelephone("123456789");

        Customer otherCustomer = new Customer();
        otherCustomer.setId(2L);
        when(userService.findByEmail("different@example.com")).thenReturn(Optional.of(otherCustomer));
        when(userService.authorizeUserOrAdmin(customerId)).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(put("/api/auth/customers/{customerId}", customerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(request)))
                .andReturn();
        });

        Throwable rootCause = exception;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        assertTrue(rootCause instanceof IllegalAccessError);
        assertEquals("This email is of other user", rootCause.getMessage());
    }

    @Test
    void testUpdatePasswordCustomer() throws Exception {
        Long userId = 1L;
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setNewPassword("newPassword");
        request.setConfirmPassword("newPassword");

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Customer");
        customer.setEmail("customer@example.com");
        customer.setDni("12345678A");
        customer.setIsActive(true);
        customer.setPlan(Plan.newPlanFree());


        when(userService.changePassword(userId, request)).thenReturn(customer);
        when(jwtUtils.generateJwtToken(Mockito.any(UserDetailsImpl.class))).thenReturn("dummy-jwt");

        mockMvc.perform(put("/api/auth/password/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("dummy-jwt"))
            .andExpect(jsonPath("$.id").value(userId))
            .andExpect(jsonPath("$.username").value("customer@example.com"))
            .andExpect(jsonPath("$.name").value("Customer"));
    }

    @Test
    void testUpdatePasswordCustomerValidationError() throws Exception {
        Long userId = 1L;
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        request.setNewPassword("newPassword");
        request.setConfirmPassword("differentPassword");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/auth/password/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCompany() throws Exception {
        Long companyId = 1L;
        
        Company company = new Company();
        company.setId(companyId);
        company.setName("Company A");
        company.setEmail("companya@example.com");
        company.setAddress("123 Main St");
        company.setCity("Test City");
        company.setZipCode("12345");
        company.setNif("A1234567B");
        
        Company currentCompanyUser = new Company();
        currentCompanyUser.setId(companyId);
        currentCompanyUser.setEmail("companya@example.com");
        currentCompanyUser.setName("Company A");
        currentCompanyUser.setAddress("123 Main St");
        currentCompanyUser.setCity("Test City");
        currentCompanyUser.setZipCode("12345");
        currentCompanyUser.setNif("A1234567B");
        
        Mockito.when(userService.findCurrentUser()).thenReturn(currentCompanyUser);
        Mockito.when(companyService.findById(companyId)).thenReturn(company);
        
        mockMvc.perform(get("/api/auth/companies/{companyId}", companyId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Company A"));
    }

    @Test
    void testGetCompanyNotFound() throws Exception {
        Long companyId = 1L;
        when(userService.authorizeUser(Mockito.anyLong())).thenReturn(null);
        when(companyService.findById(companyId)).thenThrow(new RuntimeException("Company not found"));
        
        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/api/auth/companies/{companyId}", companyId))
                .andReturn();
        });
        
        Throwable rootCause = exception;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        
        assertTrue(rootCause instanceof RuntimeException);
        assertEquals("Company not found", rootCause.getMessage());
    }


    @Test
    void testUpdateCompany() throws Exception {
        Company existingCompany = new Company();
        existingCompany.setId(1L);
        existingCompany.setName("Old Name");
        existingCompany.setEmail("old@example.com");
        existingCompany.setTelephone("111111111");
        existingCompany.setAddress("Old Address");
        existingCompany.setCity("Old City");
        existingCompany.setZipCode("00000");
        existingCompany.setImageUrl("old_image_url");
        existingCompany.setDescription("Old Description");
        existingCompany.setNif("A1234567B");
        existingCompany.setCompanyType(CompanyType.OTRO);

        CompanyUpdateRequest updateRequest = new CompanyUpdateRequest();
        updateRequest.setName("New Name");
        updateRequest.setEmail("new@example.com");
        updateRequest.setTelephone("222222222");
        updateRequest.setAddress("New Address");
        updateRequest.setCity("New City");
        updateRequest.setZipCode("12345");
        updateRequest.setImageUrl("new_image_url");
        updateRequest.setDescription("New Description");

        CompanyRepository mockRepository = Mockito.mock(CompanyRepository.class);
        when(mockRepository.findById(1L)).thenReturn(Optional.of(existingCompany));
        when(mockRepository.save(existingCompany)).thenReturn(existingCompany);

        CompanyService companyService = new CompanyService(mockRepository);

        Company updatedCompany = companyService.update(1L, updateRequest);

        assertEquals("New Name", updatedCompany.getName());
        assertEquals("new@example.com", updatedCompany.getEmail());
        assertEquals("222222222", updatedCompany.getTelephone());
        assertEquals("New Address", updatedCompany.getAddress());
        assertEquals("New City", updatedCompany.getCity());
        assertEquals("12345", updatedCompany.getZipCode());
        assertEquals("new_image_url", updatedCompany.getImageUrl());
        assertEquals("New Description", updatedCompany.getDescription());
    }

    @Test
    void testDeleteUser() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setName("Test User");
        
        Mockito.when(userService.findCurrentUser()).thenReturn(user);
        
        mockMvc.perform(delete("/api/auth/{userId}", userId))
            .andExpect(status().isNoContent());
        Mockito.verify(userService).delete(userId);
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        Long userId = 1L;
        when(userService.authorizeUser(userId)).thenReturn(null);
        doThrow(new RuntimeException("User not found")).when(userService).delete(userId);
        
        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(delete("/api/auth/{userId}", userId))
                .andReturn();
        });
        
        Throwable rootCause = exception;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        
        assertTrue(rootCause instanceof RuntimeException);
        assertEquals("User not found", rootCause.getMessage());
    }

    @Test
    void testGetCustomers() throws Exception {
        Admin admin = new Admin();
        admin.setId(99L);
        admin.setEmail("admin@example.com");
        admin.setName("Admin User");
        admin.setPassword("encodedPassword");
        
        Authentication auth = new UsernamePasswordAuthenticationToken(
            admin,
            null,
            List.of(new SimpleGrantedAuthority("ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        Mockito.when(userService.findCurrentUser()).thenReturn(admin);
        
        Customer customerA = new Customer();
        customerA.setId(1L);
        customerA.setName("Customer A");
        customerA.setEmail("customerA@example.com");
        customerA.setDni("12345678A");
        customerA.setIsActive(true);
        customerA.setPlan(Plan.newPlanFree());
        
        Customer customerB = new Customer();
        customerB.setId(2L);
        customerB.setName("Customer B");
        customerB.setEmail("customerB@example.com");
        customerB.setDni("12345678B");
        customerB.setIsActive(true);
        customerB.setPlan(Plan.newPlanFree());
        
        List<Customer> customers = List.of(customerA, customerB);
        Mockito.when(customerService.findAll()).thenReturn(customers);
        
        mockMvc.perform(get("/api/auth/admin/customers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Customer A"))
            .andExpect(jsonPath("$[1].name").value("Customer B"));
    }

    @Test
    void testGetCustomerByAdmin() throws Exception {
        Long customerId = 1L;
    
        Admin admin = new Admin();
        admin.setId(99L);
        admin.setEmail("admin@example.com");
        admin.setName("Admin User");
        admin.setPassword("encodedPassword");
    
        Authentication auth = new UsernamePasswordAuthenticationToken(
            admin,
            null,
            List.of(new SimpleGrantedAuthority("ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    
        Mockito.when(userService.findCurrentUser()).thenReturn(admin);
    
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("Customer A");
        customer.setEmail("customerA@example.com");
        customer.setDni("12345678X");
        customer.setIsActive(true);
        customer.setPlan(Plan.newPlanFree());
    
        Mockito.when(customerService.findById(customerId)).thenReturn(customer);
    
        mockMvc.perform(get("/api/auth/admin/customers/{customerId}", customerId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Customer A"));
    }
    
    @Test
    void testGetCustomerUnauthorized() throws Exception {
        Long customerId = 1L;
        doThrow(new RuntimeException("You can't access this data"))
            .when(userService).authorizeUserOrAdmin(Mockito.eq(customerId), Mockito.eq("You can't access this data"));
    
        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(get("/api/auth/customers/{customerId}", customerId))
                   .andReturn();
        });
    
        Throwable rootCause = exception;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        assertTrue(rootCause instanceof RuntimeException);
        assertEquals("You can't access this data", rootCause.getMessage());
    }
    


    @Test
    void testUpdateCustomerByAdmin() throws Exception {
        Long customerId = 1L;
        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setEmail("updated@example.com");
        request.setFullName("Updated Name");
        request.setTelephone("555555555");

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setEmail("updated@example.com");
        customer.setName("Updated Name");
        customer.setTelephone("555555555");
        customer.setIsActive(true);
        customer.setDni("12345678X");
        customer.setPlan(Plan.newPlanFree());

        Admin admin = new Admin();
        admin.setId(99L);
        admin.setEmail("admin@example.com");
        admin.setName("Admin User");
        admin.setPassword("encodedPassword");

        Authentication auth = new UsernamePasswordAuthenticationToken(
                admin,
                null,
                List.of(new SimpleGrantedAuthority("ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        Mockito.when(userService.findCurrentUser()).thenReturn(admin);
        when(customerService.update(customerId, request)).thenReturn(customer);

        mockMvc.perform(put("/api/auth/admin/customers/{customerId}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(customerId))
            .andExpect(jsonPath("$.email").value("updated@example.com"))
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.telephone").value("555555555"));
    }

    @Test
    void testGetCompanies() throws Exception {
        Company company1 = new Company();
        company1.setId(1L);
        company1.setName("Company A");
        company1.setEmail("companya@example.com");
        company1.setAddress("Address A");
        company1.setCity("City A");
        company1.setZipCode("ZipA");
        company1.setNif("NIFA");
        company1.setPlan(Plan.newPlanFree());
        company1.setCompanyType(CompanyType.OTRO);

        Company company2 = new Company();
        company2.setId(2L);
        company2.setName("Company B");
        company2.setEmail("companyb@example.com");
        company2.setAddress("Address B");
        company2.setCity("City B");
        company2.setZipCode("ZipB");
        company2.setNif("NIFB");
        company2.setPlan(Plan.newPlanFree());
        company2.setCompanyType(CompanyType.OTRO);

        List<Company> companies = List.of(company1, company2);
        when(companyService.findAll()).thenReturn(companies);

        mockMvc.perform(get("/api/auth/admin/companies")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Company A"))
            .andExpect(jsonPath("$[1].name").value("Company B"));
    }


    @Test
    void testGetCompanyByAdmin() throws Exception {
        Long companyId = 1L;
        
        Admin admin = new Admin();
        admin.setId(99L);
        admin.setEmail("admin@example.com");
        admin.setName("Admin User");
        admin.setPassword("encodedPassword");
    
        Authentication auth = new UsernamePasswordAuthenticationToken(
            admin,
            null,
            List.of(new SimpleGrantedAuthority("ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    
        Mockito.when(userService.findCurrentUser()).thenReturn(admin);

        Company company = new Company();
        company.setId(1L);
        company.setName("Company A");
        company.setEmail("companya@example.com");
        company.setAddress("Street 123");
        company.setCity("City");
        company.setZipCode("12345");
        company.setNif("A2345678Z");
        company.setPlan(Plan.newPlanFree());
        company.setCompanyType(CompanyType.OTRO);
        Mockito.when(companyService.findById(companyId)).thenReturn(company);
    
        mockMvc.perform(get("/api/auth/admin/companies/{companyId}", companyId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Company A"));
    }

    @Test
    void testUpdateCompanyByAdmin() throws Exception {
        Long companyId = 1L;
        CompanyUpdateRequest request = new CompanyUpdateRequest();
        request.setName("New Company Name");
        request.setEmail("newcompany@example.com");
        request.setTelephone("987654321");
        request.setAddress("New Address");
        request.setCity("New City");
        request.setZipCode("54321");
        request.setImageUrl("new_image_url");
        request.setDescription("New Description");
        request.setPassword("newPassword123");

        Company updatedCompany = new Company();
        updatedCompany.setId(companyId);
        updatedCompany.setName("New Company Name");
        updatedCompany.setEmail("newcompany@example.com");
        updatedCompany.setTelephone("987654321");
        updatedCompany.setAddress("New Address");
        updatedCompany.setCity("New City");
        updatedCompany.setZipCode("54321");
        updatedCompany.setImageUrl("new_image_url");
        updatedCompany.setDescription("New Description");
        updatedCompany.setNif("A1234567B");
        updatedCompany.setPlan(Plan.newPlanFree());
        updatedCompany.setCompanyType(CompanyType.OTRO);

        Admin admin = new Admin();
        admin.setId(99L);
        admin.setEmail("admin@example.com");
        admin.setName("Admin User");
        admin.setPassword("encodedPassword");

        Authentication auth = new UsernamePasswordAuthenticationToken(
                admin,
                null,
                List.of(new SimpleGrantedAuthority("ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userService.findCurrentUser()).thenReturn(admin);
        when(companyService.update(companyId, request)).thenReturn(updatedCompany);

        mockMvc.perform(put("/api/auth/admin/companies/{companyId}", companyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(companyId))
            .andExpect(jsonPath("$.name").value("New Company Name"))
            .andExpect(jsonPath("$.email").value("newcompany@example.com"))
            .andExpect(jsonPath("$.telephone").value("987654321"))
            .andExpect(jsonPath("$.address").value("New Address"))
            .andExpect(jsonPath("$.city").value("New City"))
            .andExpect(jsonPath("$.zipCode").value("54321"))
            .andExpect(jsonPath("$.imageUrl").value("new_image_url"))
            .andExpect(jsonPath("$.description").value("New Description"));
    }

    @Test
    void testDeleteAdmin() throws Exception {
        Long userId = 2L;
        User user = new User();
        user.setId(userId);
        user.setEmail("example@gmail.com");
        user.setName("Test User");
        user.setPassword("encodedPassword");
        

        Admin admin = new Admin();
        admin.setId(1L);
        admin.setEmail("admin@example.com");
        admin.setName("Admin User");
        admin.setPassword("encodedPassword");

        Authentication auth = new UsernamePasswordAuthenticationToken(
            admin, 
            null, 
            List.of(new SimpleGrantedAuthority("ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        Mockito.when(userService.findCurrentUser()).thenReturn(admin);

        mockMvc.perform(delete("/api/auth/admin/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());

        Mockito.verify(userService).delete(userId);
    }

    @Test
    void testDeleteAdminUserNotFound() throws Exception {
        Long userId = 2L;
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setEmail("admin@example.com");
        admin.setName("Admin User");
        admin.setPassword("encodedPassword");
        
        Authentication auth = new UsernamePasswordAuthenticationToken(admin, null, List.of(new SimpleGrantedAuthority("ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userService.findCurrentUser()).thenReturn(admin);
        doThrow(new RuntimeException("User not found")).when(userService).delete(userId);
        
        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(delete("/api/auth/admin/users/{userId}", userId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
        });
        
        Throwable rootCause = exception;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        
        assertTrue(rootCause instanceof RuntimeException);
        assertEquals("User not found", rootCause.getMessage());
    }


}
