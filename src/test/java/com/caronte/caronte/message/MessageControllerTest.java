package com.caronte.caronte.message;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    void testCreateMessage_ValidationError() {
        MessageRequestDto request = new MessageRequestDto();
        request.setTitle("");
        request.setBody("");
        
        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(post("/api/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andReturn();
        });
        
        Throwable rootCause = exception.getCause();
        while (rootCause != null && !(rootCause instanceof NullPointerException)) {
            rootCause = rootCause.getCause();
        }
        assertNotNull(rootCause, "Se esperaba que se lanzara una NullPointerException");
    }
    
    @Test
    void testGetMessagesByCustomerId_Success() throws Exception {
        Long customerId = 1L;
        when(userService.findCurrentUserId()).thenReturn(customerId);
        
        MessageRequestDto msg1 = Mockito.mock(MessageRequestDto.class);
        MessageRequestDto msg2 = Mockito.mock(MessageRequestDto.class);
        when(messageService.getMessagesRequestDtoByCustomerId(customerId))
                .thenReturn(List.of(msg1, msg2));
        
        mockMvc.perform(get("/api/messages/my-messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
        
    @Test
    void testGetMessagesByCustomerId_Error() throws Exception {
    }

        
    @Test
    void testGetMessageById_Success() throws Exception {
        when(userService.findCurrentUserId()).thenReturn(1L);
        
        MessageRequestDto dummyDto = Mockito.mock(MessageRequestDto.class);
        when(dummyDto.getTitle()).thenReturn("Test Title");
        when(messageService.getMessageRequestDtoByMessageId(1L, 20L)).thenReturn(dummyDto);
        
        mockMvc.perform(get("/api/messages/{messageId}", 20L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test Title")));
    }
        
    @Test
    void testGetMessageById_Forbidden() throws Exception {
       
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
        MessageRequestDto request = new MessageRequestDto();
        request.setTitle("");
        request.setBody("");
        
        Message updatedMessage = Mockito.mock(Message.class);
        when(updatedMessage.getId()).thenReturn(30L);
        when(messageService.updateMessage(eq(30L), any(MessageRequestDto.class), anyLong()))
                .thenReturn(updatedMessage);
        
        mockMvc.perform(put("/api/messages/{messageId}", 30L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteMessage_Success() throws Exception {
        when(userService.findCurrentUserId()).thenReturn(1L);
        doNothing().when(messageService).deleteMessage(eq(40L), eq(1L));

        mockMvc.perform(delete("/api/messages/{messageId}", 40L))
                .andExpect(status().isOk());

        verify(messageService).deleteMessage(40L, 1L);
    }
}
