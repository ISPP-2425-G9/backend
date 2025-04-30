package com.caronte.caronte.deathCertificate;

import com.caronte.caronte.deathCertificate.DTOs.DeathCertificateRequestDTO;
import com.caronte.caronte.deathCertificate.DTOs.DeathCertificateWithObituaryDniDTO;
import com.caronte.caronte.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DeathCertificateControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private DeathCertificateService deathCertificateService;

    @Mock
    private UserService userService;

    @InjectMocks
    private DeathCertificateController deathCertificateController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(deathCertificateController).build();
    }

    @Test
    void testUploadDeathCertificate() throws Exception {
        DeathCertificateRequestDTO requestDTO = new DeathCertificateRequestDTO();
        requestDTO.setFile("data:image/png;base64,validData");
        requestDTO.setDni("12345678A");
        requestDTO.setIsVerificate(false);

        DeathCertificate dummyCert = new DeathCertificate();
        dummyCert.setUrl("https://example.com/certificate.png");

        when(deathCertificateService.createDeathCertificateAndRelations(any(DeathCertificateRequestDTO.class), eq(null)))
                .thenReturn(dummyCert);

        mockMvc.perform(post("/api/deathCertificate/upload")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Death Certificate uploaded successfully"));
    }

    @Test
    void testUploadDeathCertificateLogged() throws Exception {
        DeathCertificateRequestDTO requestDTO = new DeathCertificateRequestDTO();
        requestDTO.setFile("data:image/png;base64,validData");
        requestDTO.setDni("12345678A");
        requestDTO.setIsVerificate(false);


        when(userService.findCurrentUserId()).thenReturn(1L);

        DeathCertificate dummyCert = new DeathCertificate();
        dummyCert.setUrl("https://example.com/certificate.png");

        when(deathCertificateService.createDeathCertificateAndRelations(any(DeathCertificateRequestDTO.class), eq(1L)))
                .thenReturn(dummyCert);

        mockMvc.perform(post("/api/deathCertificate/upload/loggedInUser")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Death Certificate uploaded successfully"));
    }

    @Test
    void testGetAllDeathCertificates() throws Exception {
       
        DeathCertificate cert1 = new DeathCertificate();
        cert1.setUrl("https://example.com/certificate1.png");
        DeathCertificate cert2 = new DeathCertificate();
        cert2.setUrl("https://example.com/certificate2.png");

        List<DeathCertificate> certList = List.of(cert1, cert2);
        when(deathCertificateService.getAllDeathCertificates()).thenReturn(certList);

        mockMvc.perform(get("/api/deathCertificate/all")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].url", is("https://example.com/certificate1.png")))
                .andExpect(jsonPath("$[1].url", is("https://example.com/certificate2.png")));
    }

    @Test
    void testGetDeathCertificateByObituaryId() throws Exception {
       
        DeathCertificateWithObituaryDniDTO dummyDTO = new DeathCertificateWithObituaryDniDTO();
        dummyDTO.setDni("12345678A");

        DeathCertificate dummyCert = new DeathCertificate();
        dummyCert.setUrl("https://example.com/certificate.png");
        dummyDTO.setDeathCertificate(dummyCert);

        when(deathCertificateService.getDeathCertificateByObituaryId(1L)).thenReturn(dummyDTO);

        mockMvc.perform(get("/api/deathCertificate/obituary/1")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni", is("12345678A")))
                .andExpect(jsonPath("$.deathCertificate.url", is("https://example.com/certificate.png")));
    }
}
