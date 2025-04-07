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

import com.caronte.caronte.user.User;

class PlanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PlanService planService;

    @InjectMocks
    private PlanController planController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(planController).build();
    }

    @Test
    @WithMockUser
    void testChangePlan_Success() throws Exception {
        // Simular comportamiento del servicio
        when(planService.changePlan(any(User.class), any(PlanType.class), any(String.class))).thenReturn(Plan.newPlanPremium("sub_4fbc23afe..."));

        // Crear el objeto que simula el RequestBody (ChangePlanRequest)
        String requestBody = "{ \"planType\": \"Premium\", \"paymentMethodId\": \"pm_1Je1wF2eZvKYlo2Cl39B5gF1\", \"isPremium\": true }";

        // Realizar la petición al endpoint del controlador
        mockMvc.perform(put("/plans/{userId}", 1L) // Usar PUT y el path correspondiente
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").exists()) // Verificar que se genera el JWT
                .andExpect(jsonPath("$.user").exists()); // Verificar que la respuesta tiene información del usuario

        // Verificar que el servicio fue llamado una vez
        verify(planService, times(1)).changePlan(any(User.class), any(PlanType.class), any(String.class));
    }
}
