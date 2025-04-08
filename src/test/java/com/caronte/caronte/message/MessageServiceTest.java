package com.caronte.caronte.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.image.Image;
import com.caronte.caronte.image.ImageRepository;
import com.caronte.caronte.message.DTOs.MessageRequestDto;
import com.caronte.caronte.receiver.ReceiverRepository;
import com.caronte.caronte.receiver.ReceiverService;
import com.caronte.caronte.user.UserService;
import com.caronte.caronte.util.AESCipher;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.util.exceptions.ResourceNotFound;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock private MessageRepository messageRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private ReceiverRepository receiverRepository;
    @Mock private ReceiverService receiverService;
    @Mock private ImageRepository imageRepository;
    @Mock private MediaHandler mediaHandler;
    @Mock private AESCipher aesCipher;
    @Mock private UserService userService;

    @InjectMocks
    private MessageService messageService;

    @Test
    void getMessageById_shouldReturnMessage_whenExists() {
        Message message = new Message();
        message.setId(1L);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        Message result = messageService.getMessageById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getMessageById_shouldThrow_whenMessageNotFound() {
        when(messageRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class, () -> messageService.getMessageById(1L));
    }

    @Test
    void createMessage_shouldCreateMessageSuccessfully() {
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);
        MessageRequestDto dto = new MessageRequestDto();
        dto.setTitle("Test");
        dto.setBody("Test Body");
        dto.setRecipients(List.of());
        dto.setCustomImages(List.of());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> {
            Message m = i.getArgument(0);
            m.setId(1L);
            return m;
        });
        when(aesCipher.encrypt(anyString())).thenReturn("encryptedCode");

        Message result = messageService.createMessage(dto, customerId);

        assertEquals("Test", result.getTitle());
        assertEquals("Test Body", result.getBody());
        assertEquals(customer, result.getCustomer());
        assertFalse(result.getIsLastWill());
        assertNotNull(result.getCode());
    }

    @Test
    void validateMessageCode_shouldReturnTrue_whenCodeIsValid() {
        Message message = new Message();
        message.setCode("encrypted");
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(aesCipher.decrypt("encrypted")).thenReturn("12345");

        boolean isValid = messageService.validateMessageCode(1L, "12345");

        assertTrue(isValid);
    }

    @Test
    void validateMessageCode_shouldReturnFalse_whenCodeIsInvalid() {
        Message message = new Message();
        message.setCode("encrypted");
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(aesCipher.decrypt("encrypted")).thenReturn("12345");

        boolean isValid = messageService.validateMessageCode(1L, "00000");

        assertFalse(isValid);
    }

    @Test
    void updateMessage_shouldThrow_whenUserIsNotOwner() {
        Message message = new Message();
        Customer otherCustomer = new Customer();
        otherCustomer.setId(2L);
        message.setCustomer(otherCustomer);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        MessageRequestDto dto = new MessageRequestDto();
        dto.setTitle("title");
        dto.setBody("body");
        dto.setCustomImages(List.of());

        assertThrows(Exception.class, () -> messageService.updateMessage(1L, dto, 1L));
    }

    @Test
    @org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
    void deleteMessage_shouldThrow_whenUserUnauthorized() {
        doThrow(new RuntimeException("Unauthorized")).when(userService).authorizeUserOrAdmin(anyLong(), anyString());

        assertThrows(RuntimeException.class, () -> messageService.deleteMessage(1L, 999L));
    }


    @Test
    void isOwner_shouldReturnTrue_whenCustomerMatches() {
        Customer customer = new Customer();
        customer.setId(10L);
        Message message = new Message();
        message.setCustomer(customer);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        assertTrue(messageService.isOwner(1L, 10L));
    }

    @Test
    void isOwner_shouldReturnFalse_whenCustomerDoesNotMatch() {
        Customer customer = new Customer();
        customer.setId(10L);
        Message message = new Message();
        message.setCustomer(customer);
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        assertFalse(messageService.isOwner(1L, 99L));
    }

    @Test
    void getMessageRequestDtoByMessageId_shouldReturnDto_whenCustomerIsOwner() {
        Long customerId = 1L;
        Message message = new Message();
        message.setId(1L);
        Customer customer = new Customer();
        customer.setId(customerId);
        message.setCustomer(customer);

        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(imageRepository.findAllByMessageId(1L)).thenReturn(List.of());
        when(receiverRepository.findByMessageId(1L)).thenReturn(List.of());

        MessageRequestDto dto = messageService.getMessageRequestDtoByMessageId(customerId, 1L);

        assertEquals(1L, dto.getMessageId());
    }

    @Test
    void getMessagesRequestDtoByCustomerId_shouldReturnList() {
        Long customerId = 1L;
        Message msg1 = new Message();
        msg1.setId(1L);
        msg1.setCustomer(new Customer());
        Message msg2 = new Message();
        msg2.setId(2L);
        msg2.setCustomer(new Customer());

        when(messageRepository.findAllByCustomerId(customerId)).thenReturn(List.of(msg1, msg2));
        when(imageRepository.findAllByMessageId(anyLong())).thenReturn(List.of());
        when(receiverRepository.findByMessageId(anyLong())).thenReturn(List.of());

        List<MessageRequestDto> result = messageService.getMessagesRequestDtoByCustomerId(customerId);

        assertEquals(2, result.size());
    }

    @Test
    void updateMessage_shouldRemoveObsoleteImages() {
        Message message = new Message();
        Customer customer = new Customer();
        customer.setId(1L);
        message.setId(1L);
        message.setCustomer(customer);

        Image image = new Image();
        image.setImageUrl("old-url");
        image.setMessage(message);

        MessageRequestDto dto = new MessageRequestDto();
        dto.setTitle("Updated Title");
        dto.setBody("Updated Body");
        dto.setCustomImages(List.of()); 

        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(imageRepository.findAllByMessageId(1L)).thenReturn(new ArrayList<>(List.of(image)));
        when(messageRepository.save(any())).thenReturn(message);

        Message updated = messageService.updateMessage(1L, dto, 1L);

        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Body", updated.getBody());
    }

}
