package com.caronte.caronte.obituary;

import com.caronte.caronte.obituary.DTOs.ObituraryRequestDto;
import com.caronte.caronte.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

public class ObituaryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ObituaryService obituaryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ObituaryController obituaryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(obituaryController).build();
    }

    @Test
    void testCreateObituary() throws Exception {
        ObituraryRequestDto request = new ObituraryRequestDto();
        request.setName("John Doe"); 
        request.setBirthDate(LocalDate.of(1980, 5, 10));
        request.setDeathDate(LocalDate.of(2023, 1, 1));
        request.setFarewellMessage("A farewell message");
        request.setFarewellPhrase("Rest in peace"); 
        request.setImageTemplate_id(1L);
        request.setIsMine(true); 
        request.setWordColor("255,255,255"); 
        
        ObituraryRequestDto.ContactDto contact = new ObituraryRequestDto.ContactDto();
        contact.setName("Jane Doe"); 
        contact.setPhone("987654321"); 
        contact.setEmail("jane.doe@example.com"); 

        request.setContacts(List.of(contact)); 

        when(userService.findCurrentUserId()).thenReturn(1L);
        when(obituaryService.createObituaryWithReceivers(request, 1L)).thenReturn(null);

        mockMvc.perform(post("/api/obituary/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string("Obituary created successfully"));
    }

    @Test
    void testUpdateObituary() throws Exception {
        ObituraryRequestDto request = new ObituraryRequestDto();
        request.setName("Updated Name"); 
        request.setBirthDate(LocalDate.of(1985, 7, 20));
        request.setDeathDate(LocalDate.of(2024, 2, 15));
        request.setFarewellMessage("Updated farewell message");
        request.setFarewellPhrase("Updated phrase"); 
        request.setImageTemplate_id(2L);
        request.setIsMine(false); 
        request.setWordColor("128,128,128"); 
        
        ObituraryRequestDto.ContactDto contact = new ObituraryRequestDto.ContactDto();
        contact.setName("Updated Contact"); 
        contact.setPhone("123456789"); 
        contact.setEmail("updated.contact@example.com"); 

        request.setContacts(List.of(contact)); 

        when(userService.findCurrentUserId()).thenReturn(1L);
        when(obituaryService.updateObituaryWithReceivers(1L,1L,request)).thenReturn(null);

        mockMvc.perform(put("/api/obituary/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string("Obituary updated successfully"));
    }
    @Test
    void testDeleteObituary() throws Exception {
        when(userService.findCurrentUserId()).thenReturn(1L);
        doNothing().when(obituaryService).deleteObituaryByCustomer(1L, 1L);

        mockMvc.perform(delete("/api/obituary/delete/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Obituary deleted successfully"));
    }

    @Test
    void testGetAllObituariesByCustomer() throws Exception {
        when(userService.findCurrentUserId()).thenReturn(1L);
        when(obituaryService.getAllObituariesByCustomer(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/obituary/myObituaries")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testGetObituaryById() throws Exception {
        when(userService.findCurrentUserId()).thenReturn(1L);
        Obituary obituary = new Obituary();
        obituary.setName("John Doe");
        obituary.setCustomer(null);

        when(obituaryService.getObituaryById(1L, 1L)).thenReturn(obituary);

        mockMvc.perform(get("/api/obituary/myObituaries/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    private static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
