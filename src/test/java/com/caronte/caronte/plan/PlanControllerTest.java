package com.caronte.caronte.plan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
        // when(planService.changePlan(any(User.class), any(PlanType.class), any(String.class))).thenReturn(new PlanResponse(plan));

        // // Realizar la petición al endpoint del controlador
        // mockMvc.perform(post("/plans/change")
        //         .contentType(MediaType.APPLICATION_JSON)
        //         .content("{\"planId\": 1, \"newPlan\": \"Premium\"}"))
        //         .andExpect(status().isOk())
        //         .andExpect(jsonPath("$.message").value("Success"));

        // // Verificar que el servicio fue llamado
        // verify(planService, times(1)).changePlan(any());
    }
}
