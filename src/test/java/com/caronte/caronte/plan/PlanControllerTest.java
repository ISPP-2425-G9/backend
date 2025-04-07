package com.caronte.caronte.plan;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.caronte.caronte.configuration.jwt.JwtUtils;
import com.caronte.caronte.configuration.services.StripeService;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.plan.DTOs.ChangePlanRequest;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

class PlanControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PlanService planService;

    @InjectMocks
    private PlanController planController;

    @Mock
    private StripeService stripeService; 

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(planController).build();
    }

    @Test
    @WithMockUser(username = "testUser", authorities = {"CUSTOMER"})
    void testChangePlan_Success() throws Exception {
        Customer mockUser = new Customer();
        mockUser.setPlan(Plan.newPlanFree());

        when(userService.authorizeUserOrAdmin(anyLong())).thenReturn(mockUser);
        when(planService.changePlan(any(User.class), any(PlanType.class), any(String.class))).thenReturn(Plan.newPlanPremium("sub_4fbc23afe..."));
        when(stripeService.subscription(any(String.class), any(User.class))).thenReturn("sub_4fbc23afe...");
        String mockJwt = "mock-jwt-token";
        when(jwtUtils.generateJwtToken(any(UserDetailsImpl.class))).thenReturn(mockJwt);
        when(userService.findCurrentUser()).thenReturn(mockUser);

        ChangePlanRequest changePlanRequest = new ChangePlanRequest();
        changePlanRequest.setPaymentMethodId("pm_1Je1wF2eZvKYlo2Cl39B5gF1");
        changePlanRequest.setPlanType(PlanType.PREMIUM);

        String requestBody = objectMapper.writeValueAsString(changePlanRequest);
        mockMvc.perform(put("/api/plans/{userId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(mockJwt));

        verify(userService, times(1)).authorizeUserOrAdmin(anyLong());
        verify(stripeService, times(1)).subscription(any(String.class), any(User.class));
        verify(jwtUtils, times(1)).generateJwtToken(any(UserDetailsImpl.class));
    }
}
