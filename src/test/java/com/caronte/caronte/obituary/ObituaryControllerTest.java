package com.caronte.caronte.obituary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.caronte.caronte.configuration.services.UserDetailsImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ObituaryControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ObituaryService obituaryService;

    @MockBean
    private Authentication authentication;

    @InjectMocks
    private ObituaryController obituaryController;

    private UserDetailsImpl userDetailsImpl;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        userDetailsImpl = mock(UserDetailsImpl.class);
        mockMvc = MockMvcBuilders.standaloneSetup(obituaryController).build();
    }

    @Test
public void testCreateObituary_Success() throws Exception {
    // Crear un objeto ObituraryRequestDto con datos válidos
    ObituraryRequestDto requestDto = new ObituraryRequestDto();
    requestDto.setName("John Doe");
    requestDto.setFarewellMessage("Goodbye!");
    requestDto.setFarewellPhrase("Rest in peace.");
    requestDto.setIsMine(true);
    requestDto.setImageTemplate_id(1L);
    requestDto.setBirthDate(LocalDate.of(2000, 1, 1));  // Fecha como objeto LocalDate
    requestDto.setDeathDate(LocalDate.of(2021, 1, 1));  // Fecha como objeto LocalDate
    requestDto.setWordColor("255,255,255");  // RGB válido
    requestDto.setCustomImage("http://example.com/image.jpg");

    // Crear un contacto válido
    ObituraryRequestDto.ContactDto contact = new ObituraryRequestDto.ContactDto();
    contact.setName("Jane Doe");
    contact.setPhone("123456789");
    contact.setEmail("jane.doe@example.com");
    requestDto.setContacts(List.of(contact));

    // Mockear la autenticación
    when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
    when(userDetailsImpl.getId()).thenReturn(1L);

    // Convertir el DTO a JSON
    String requestBody = objectMapper.writeValueAsString(requestDto);

    // Realizar la solicitud
    mockMvc.perform(post("/api/obituary/create")
            .header("Authorization", "Bearer token123")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))  // Usar el JSON generado
            .andExpect(status().isOk())  // Esperar respuesta 200 OK
            .andExpect(jsonPath("$.message").value("Obituary created successfully"));

    // Verificar que el servicio fue llamado correctamente
    verify(obituaryService, times(1)).createObituaryWithReceivers(any(), eq(1L));
}


    @Test
    public void testCreateObituary_Error() throws Exception {
        when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
        when(userDetailsImpl.getId()).thenReturn(1L);
        doThrow(new RuntimeException("Internal error")).when(obituaryService).createObituaryWithReceivers(any(), eq(1L));

        mockMvc.perform(post("/api/obituary/create")
                .header("Authorization", "Bearer token123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\",\"farewellMessage\":\"Goodbye!\",\"farewellPhrase\":\"Rest in peace.\",\"isMine\":true}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal error"));

        verify(obituaryService, times(1)).createObituaryWithReceivers(any(), eq(1L));
    }

    @Test
    public void testUpdateObituary_Success() throws Exception {
        ObituraryRequestDto requestDto = new ObituraryRequestDto();
        requestDto.setName("Updated Name");
        requestDto.setFarewellMessage("Updated message");
        requestDto.setFarewellPhrase("Updated phrase");
        requestDto.setIsMine(true);

        when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
        when(userDetailsImpl.getId()).thenReturn(1L);

        mockMvc.perform(put("/api/obituary/update/{obituary_id}", 1L)
                .header("Authorization", "Bearer token123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Name\",\"farewellMessage\":\"Updated message\",\"farewellPhrase\":\"Updated phrase\",\"isMine\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Obituary updated successfully"));

        verify(obituaryService, times(1)).updateObituaryWithReceivers(eq(1L), eq(1L), any());
    }

    @Test
    public void testUpdateObituary_Failure() throws Exception {
        ObituraryRequestDto requestDto = new ObituraryRequestDto();
        requestDto.setName("Updated Name");
        requestDto.setFarewellMessage("Updated message");
        requestDto.setFarewellPhrase("Updated phrase");
        requestDto.setIsMine(true);

        when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
        when(userDetailsImpl.getId()).thenReturn(1L);
        doThrow(new IllegalArgumentException("You can't change IsMine property")).when(obituaryService)
                .updateObituaryWithReceivers(eq(1L), eq(1L), any());

        mockMvc.perform(put("/api/obituary/update/{obituary_id}", 1L)
                .header("Authorization", "Bearer token123")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Name\",\"farewellMessage\":\"Updated message\",\"farewellPhrase\":\"Updated phrase\",\"isMine\":true}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("You can't change IsMine property"));

        verify(obituaryService, times(1)).updateObituaryWithReceivers(eq(1L), eq(1L), any());
    }

    @Test
    public void testDeleteObituary_Success() throws Exception {
        when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
        when(userDetailsImpl.getId()).thenReturn(1L);

        mockMvc.perform(delete("/api/obituary/delete/{obituary_id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Obituary deleted successfully"));

        verify(obituaryService, times(1)).deleteObituaryByCustomer(eq(1L), eq(1L));
    }

    @Test
    public void testDeleteObituary_Failure() throws Exception {
        when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
        when(userDetailsImpl.getId()).thenReturn(1L);
        doThrow(new IllegalArgumentException("You are not allowed to delete this obituary")).when(obituaryService)
                .deleteObituaryByCustomer(eq(1L), eq(1L));

        mockMvc.perform(delete("/api/obituary/delete/{obituary_id}", 1L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("You are not allowed to delete this obituary"));

        verify(obituaryService, times(1)).deleteObituaryByCustomer(eq(1L), eq(1L));
    }

    @Test
    public void testGetAllObituariesByCustomer_Success() throws Exception {
        when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
        when(userDetailsImpl.getId()).thenReturn(1L);

        mockMvc.perform(get("/api/obituary/myObituaries")
                .header("Authorization", "Bearer token123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(obituaryService, times(1)).getAllObituariesByCustomer(eq(1L));
    }

    @Test
    public void testGetObituaryById_Success() throws Exception {
        when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
        when(userDetailsImpl.getId()).thenReturn(1L);
        Obituary obituary = new Obituary();
        obituary.setId(1L);
        when(obituaryService.getObituaryById(1L)).thenReturn(obituary);

        mockMvc.perform(get("/api/obituary/myObituaries/{obituaryId}", 1L)
                .header("Authorization", "Bearer token123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(obituaryService, times(1)).getObituaryById(eq(1L));
    }

    @Test
    public void testGetObituaryById_Failure() throws Exception {
        when(authentication.getPrincipal()).thenReturn(userDetailsImpl);
        when(userDetailsImpl.getId()).thenReturn(1L);
        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't access this obituary")).when(obituaryService)
                .getObituaryById(eq(1L));

        mockMvc.perform(get("/api/obituary/myObituaries/{obituaryId}", 1L)
                .header("Authorization", "Bearer token123"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You can't access this obituary"));

        verify(obituaryService, times(1)).getObituaryById(eq(1L));
    }
}
