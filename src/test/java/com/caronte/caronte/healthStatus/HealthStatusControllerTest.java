package com.caronte.caronte.healthStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class HealthStatusControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HealthStatusController()).build();
    }

    @Test
    public void getHealthStatus_returnsTrue() throws Exception {
        mockMvc.perform(get("/api/status")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void wrongEndpoint_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/statuss")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void wrongHttpMethod_returnsMethodNotAllowed() throws Exception {
        mockMvc.perform(post("/api/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void unsupportedAcceptHeader_returnsNotAcceptable() throws Exception {
        mockMvc.perform(get("/api/status")
                        .accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isNotAcceptable());
    }
}
