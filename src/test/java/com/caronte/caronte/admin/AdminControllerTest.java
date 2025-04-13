package com.caronte.caronte.admin;

import com.caronte.caronte.admin.DTOs.CertificateResponseDTO;
import com.caronte.caronte.admin.DTOs.MessageResponseDTO;
import com.caronte.caronte.admin.DTOs.ObituaryResponseDTO;
import com.caronte.caronte.admin.DTOs.ValidCertificateRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void testGetAllPendingDeathCertificate() throws Exception {
        CertificateResponseDTO certificate = new CertificateResponseDTO();
        certificate.setId(1L);
        certificate.setName("John Doe");

        when(adminService.getAllPendingCertificates()).thenReturn(List.of(certificate));

        mockMvc.perform(get("/api/admin/certificates/pending"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("John Doe"));
    }


    @Test
    void testGetAllObituariesByDeathCertificate() throws Exception {
        ObituaryResponseDTO obituary = new ObituaryResponseDTO();
        obituary.setId(1L);
        obituary.setName("Jane Smith");
        obituary.setFarewellMessage("Farewell message");

        when(adminService.getAllObituariesByCertificateId(1L)).thenReturn(List.of(obituary));

        mockMvc.perform(get("/api/admin/certificates/obituaries/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("Jane Smith"))
            .andExpect(jsonPath("$[0].farewellMessage").value("Farewell message"));
    }


    @Test
    void testGetAllMessagesByDeathCertificate() throws Exception {
        MessageResponseDTO message = new MessageResponseDTO();
        message.setId(1L);
        message.setTitle("Test Title");
        message.setBody("Test body");

        when(adminService.getAllMessagesByCertificateId(1L)).thenReturn(List.of(message));

        mockMvc.perform(get("/api/admin/certificates/messages/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].title").value("Test Title"))
            .andExpect(jsonPath("$[0].body").value("Test body"));
    }

    @Test
    void testApproveDeathCertificate() throws Exception {
        ValidCertificateRequestDTO request = new ValidCertificateRequestDTO();
        request.setDeathDate(LocalDate.of(2023, 1, 1));

        doNothing().when(adminService).verificateDeathCertificate(1L, request.getDeathDate());

        mockMvc.perform(put("/api/admin/certificates/approve/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string("Certificate approved successfully"));
    }
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static String asJsonString(final Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDisapproveDeathCertificate() throws Exception {
        doNothing().when(adminService).disapproveCertificate(1L);

        mockMvc.perform(delete("/api/admin/certificates/disapprove/1"))
            .andExpect(status().isOk())
            .andExpect(content().string("Certificate disapproved successfully"));
    }
}
