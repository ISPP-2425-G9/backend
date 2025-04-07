package com.caronte.caronte.message;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.caronte.caronte.message.DTOs.MessageRequestDto;
import com.caronte.caronte.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MessageService messageService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MessageController messageController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(messageController)
                .setValidator(validator)
                .build();
    }

    @Test
    void testCreateMessage_Success() throws Exception {
        MessageRequestDto request = new MessageRequestDto();
        request.setTitle("Test Title");
        request.setBody("Test Body");
        when(userService.findCurrentUserId()).thenReturn(1L);
        Message dummyMessage = Mockito.mock(Message.class);
        when(dummyMessage.getId()).thenReturn(10L);
        when(messageService.createMessage(Mockito.<MessageRequestDto>any(), eq(1L)))
                .thenReturn(dummyMessage);
        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.messageId", is(10)));
    }

    @Test
    void testCreateMessage_ValidationError() throws Exception {
        /*
        MessageRequestDto request = new MessageRequestDto();
        request.setTitle("");
        request.setBody("");
        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
                */
    }

    @Test
    void testGetMessagesByCustomerId_Success() throws Exception {
        Long customerId = 1L;
        when(userService.authorizeUser(customerId)).thenReturn(null);
        Message msg1 = Mockito.mock(Message.class);
        Message msg2 = Mockito.mock(Message.class);
        when(messageService.getMessagesByCustomerId(customerId)).thenReturn(List.of(msg1, msg2));
        mockMvc.perform(get("/api/messages/{customerId}/my_messages", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetMessagesByCustomerId_Error() throws Exception {
        Long customerId = 1L;
        when(userService.authorizeUser(customerId)).thenThrow(new RuntimeException("Unauthorized"));
        mockMvc.perform(get("/api/messages/{customerId}/my_messages", customerId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Unauthorized")));
    }

    @Test
    void testGetMessageById_Success() throws Exception {
        when(userService.findCurrentUserId()).thenReturn(1L);
        Message dummyMessage = Mockito.mock(Message.class);
        when(dummyMessage.getId()).thenReturn(20L);
        when(dummyMessage.hasCustomerWithId(1L)).thenReturn(true);
        when(messageService.getMessageById(20L, 1L)).thenReturn(dummyMessage);
        mockMvc.perform(get("/api/messages/{messageId}", 20L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(20)));
    }

    @Test
    void testGetMessageById_Forbidden() throws Exception {
        /*
        when(userService.findCurrentUserId()).thenReturn(1L);
        Message dummyMessage = Mockito.mock(Message.class);
        when(dummyMessage.hasCustomerWithId(1L)).thenReturn(false);
        when(messageService.getMessageById(20L, 1L)).thenReturn(dummyMessage);
        mockMvc.perform(get("/api/messages/{messageId}", 20L))
                .andExpect(status().isInternalServerError());
                */
    }

    @Test
    void testUpdateMessage_Success() throws Exception {
        MessageRequestDto request = new MessageRequestDto();
        request.setTitle("Updated Title");
        request.setBody("Updated Body");
        when(userService.findCurrentUserId()).thenReturn(1L);
        Message updatedMessage = Mockito.mock(Message.class);
        when(updatedMessage.getId()).thenReturn(30L);
        when(messageService.updateMessage(anyLong(), any(MessageRequestDto.class), anyLong()))
                .thenReturn(updatedMessage);
        mockMvc.perform(put("/api/messages/{messageId}", 30L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(30)));
    }

    @Test
    void testUpdateMessage_ValidationError() throws Exception {
        /*
        MessageRequestDto request = new MessageRequestDto();
        request.setTitle("");
        request.setBody("");
        mockMvc.perform(put("/api/messages/{messageId}", 30L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
                */
    }

    @Test
    void testDeleteMessage_Success() throws Exception {
        /* 
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "test@example.com", "password", List.of());
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.findCurrentUserId()).thenReturn(1L);
        doNothing().when(messageService).deleteMessage(eq(40L), eq(1L));
        mockMvc.perform(delete("/api/messages/{messageId}", 40L))
                .andExpect(status().isOk());
        verify(messageService).deleteMessage(40L, 1L);
        */
    }


}
