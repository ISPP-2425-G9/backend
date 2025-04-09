package com.caronte.caronte.emergencyContact;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.caronte.caronte.emergencyContact.DTOs.EmergencyContactDTO;
import com.caronte.caronte.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmergencyContactControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmergencyContactService emergencyContactService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EmergencyContactController emergencyContactController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(emergencyContactController)
                .setValidator(validator)
                .build();
    }

    @Test
    void testGetEmergencyContacts_Success() throws Exception {
        // Se simula que el usuario actual tiene email "test@example.com"
        String email = "test@example.com";
        when(userService.findCurrentUserEmail()).thenReturn(email);

        // Se preparan dos objetos EmergencyContactDTO de ejemplo
        EmergencyContactDTO dto1 = new EmergencyContactDTO(1L, "John Doe", "123456789", email);

        EmergencyContactDTO dto2 = new EmergencyContactDTO(2L, "Jane Doe", "987654321", email);


        // Se simula que el servicio retorna la lista con los dos contactos
        when(emergencyContactService.findAll(email)).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].telephone", is("123456789")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane Doe")))
                .andExpect(jsonPath("$[1].telephone", is("987654321")));
    }

    @Test
    void testSaveEmergencyContact_Success() throws Exception {
        String email = "test@example.com";
        when(userService.findCurrentUserEmail()).thenReturn(email);

        // Se prepara el DTO con los datos del contacto a guardar
        EmergencyContactDTO request = new EmergencyContactDTO(1L , "John Doe", "123456789", email);

        // Se simula la respuesta del servicio al guardar el contacto
        EmergencyContactDTO savedDto = new EmergencyContactDTO(1L , "John Doe", "123456789", email);

        when(emergencyContactService.save(any(EmergencyContactDTO.class), eq(email))).thenReturn(savedDto);

        mockMvc.perform(post("/api/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.telephone", is("123456789")));
    }

    @Test
    void testUpdateEmergencyContact_Success() throws Exception {
        String email = "test@example.com";
        when(userService.findCurrentUserEmail()).thenReturn(email);

        // Se prepara el DTO con los nuevos datos para actualizar
        EmergencyContactDTO request = new EmergencyContactDTO(1L , "John Updated", "111222333", email);

        // Se simula la respuesta del servicio al actualizar el contacto
        EmergencyContactDTO updatedDto = new EmergencyContactDTO(1L , "John Updated", "111222333", email);

        when(emergencyContactService.update(any(EmergencyContactDTO.class), eq(1L), eq(email)))
                .thenReturn(updatedDto);

        mockMvc.perform(put("/api/contacts/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Updated")))
                .andExpect(jsonPath("$.telephone", is("111222333")));
    }

    @Test
    void testDeleteEmergencyContact_Success() throws Exception {
        String email = "test@example.com";
        when(userService.findCurrentUserEmail()).thenReturn(email);

        // Simulamos que el servicio elimina correctamente sin retornar nada
        doNothing().when(emergencyContactService).delete(eq(1L), eq(email));

        mockMvc.perform(delete("/api/contacts/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(emergencyContactService).delete(1L, email);
    }
    
    // En caso de que EmergencyContactDTO cuente con validaciones (por ejemplo, nombre no puede ser vacío),
    // se puede agregar un test para validar el error:
    @Test
    void testSaveEmergencyContact_ValidationError() throws Exception {
        // Se asume que el nombre es obligatorio, por lo que se envía una cadena vacía
        EmergencyContactDTO request = new EmergencyContactDTO(1L, "", "123456789", null);

        mockMvc.perform(post("/api/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
