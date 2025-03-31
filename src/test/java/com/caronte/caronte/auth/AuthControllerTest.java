package com.caronte.caronte.auth;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import com.caronte.caronte.auth.payload.response.LoginRequest;
import com.caronte.caronte.auth.payload.response.RegisterRequestCompany;
import com.caronte.caronte.auth.payload.response.RegisterRequestCustomer;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyService;
import com.caronte.caronte.company.CompanyType;
import com.caronte.caronte.configuration.jwt.JwtUtils;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerService;
import com.caronte.caronte.plan.Plan;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserService;
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
    void testDeleteAdmin() throws Exception {
        Long userId = 2L;

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
    void testDeleteUser() throws Exception {
        Long userId = 1L;
        User user;
        user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setName("Test User");
        
        Mockito.when(userService.findCurrentUser()).thenReturn(user);
        
        mockMvc.perform(delete("/api/auth/{userId}", userId))
            .andExpect(status().isNoContent());
        Mockito.verify(userService).delete(userId);
    }

    @Test
    void testGetCompanies() throws Exception {
        Admin admin = new Admin();
        admin.setId(99L);
        admin.setEmail("admin@example.com");
        admin.setName("Admin User");
        
        Authentication auth = new UsernamePasswordAuthenticationToken(
            admin, 
            null, 
            List.of(new SimpleGrantedAuthority("ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        Mockito.when(userService.findCurrentUser()).thenReturn(admin);
        
        Company companyA = new Company();
        companyA.setId(1L);
        companyA.setName("Company A");
        
        Company companyB = new Company();
        companyB.setId(2L);
        companyB.setName("Company B");
        
        List<Company> companies = List.of(companyA, companyB);
        Mockito.when(companyService.findAll()).thenReturn(companies);
        
        mockMvc.perform(get("/api/auth/admin/companies"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Company A"))
            .andExpect(jsonPath("$[1].name").value("Company B"));
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
        company.setNif("NIF123");
        
        Company currentCompanyUser = new Company();
        currentCompanyUser.setId(companyId);
        currentCompanyUser.setEmail("companya@example.com");
        currentCompanyUser.setName("Company A");
        currentCompanyUser.setAddress("123 Main St");
        currentCompanyUser.setCity("Test City");
        currentCompanyUser.setZipCode("12345");
        currentCompanyUser.setNif("NIF123");
        
        Mockito.when(userService.findCurrentUser()).thenReturn(currentCompanyUser);
        Mockito.when(companyService.findById(companyId)).thenReturn(company);
        
        mockMvc.perform(get("/api/auth/companies/{companyId}", companyId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Company A"));
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
        company.setId(companyId);
        company.setName("Company A");
        company.setEmail("companya@example.com");
        Mockito.when(companyService.findById(companyId)).thenReturn(company);
    
        mockMvc.perform(get("/api/auth/admin/companies/{companyId}", companyId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Company A"));
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
        customer.setDni("DNI123");
        customer.setIsActive(true);
        customer.setPlan(Plan.newPlanFree());
    
        Mockito.when(customerService.findById(customerId)).thenReturn(customer);
    
        mockMvc.perform(get("/api/auth/admin/customers/{customerId}", customerId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Customer A"));
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
        customerA.setDni("DNI_A");
        customerA.setIsActive(true);
        customerA.setPlan(Plan.newPlanFree());
        
        Customer customerB = new Customer();
        customerB.setId(2L);
        customerB.setName("Customer B");
        customerB.setEmail("customerB@example.com");
        customerB.setDni("DNI_B");
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
        company.setNif("12345678Z");
        company.setPlan(Plan.newPlanFree());
        company.setCompanyType(CompanyType.OTRO);

        Mockito.when(authService.validateAndBuildCompany(Mockito.any(RegisterRequestCompany.class), Mockito.any()))
            .thenReturn(company);

        UserDetailsImpl userDetails = UserDetailsImpl.build(company);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
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
    void testUpdateCompany() throws Exception {
    }
    


    @Test
    void testUpdateCompanyByAdmin() throws Exception {
    }


    @Test
    void testUpdateCustomer() {

    }

    @Test
    void testUpdateCustomer2() {

    }

    @Test
    void testUpdateCustomerByAdmin() {

    }
}
