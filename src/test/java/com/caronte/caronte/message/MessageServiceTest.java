package com.caronte.caronte.message;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.image.Image;
import com.caronte.caronte.image.ImageRepository;
import com.caronte.caronte.message.DTOs.MessageRequestDto;
import com.caronte.caronte.receiver.Receiver;
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
    void getMessageRequestDtoByMessageId_shouldReturnDto_whenCustomerIsOwner() {
        Long customerId = 1L;
        Message message = new Message();
        message.setId(1L);
        message.setTitle("Test Title");
        message.setBody("Test Body");
        message.setIsLastWill(false);
        Customer customer = new Customer();
        customer.setId(customerId);
        message.setCustomer(customer);

        // Simular que hay imágenes y receivers (vacío para simplificar)
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(imageRepository.findAllByMessageId(1L)).thenReturn(Collections.emptyList());
        when(receiverRepository.findByMessageId(1L)).thenReturn(Collections.emptyList());

        MessageRequestDto dto = messageService.getMessageRequestDtoByMessageId(customerId, 1L);
        assertNotNull(dto);
        assertEquals(1L, dto.getMessageId());
        assertEquals("Test Title", dto.getTitle());
        assertEquals("Test Body", dto.getBody());
    }

    @Test
    void getMessageRequestDtoByMessageId_shouldThrowForbidden_whenNotOwner() {
        // Usar ResponseThrow (suponiendo que lanza excepción)
        Long customerId = 1L;
        Message message = new Message();
        message.setId(1L);
        // Simular que message.hasCustomerWithId(customerId) retorne false
        message.setCustomer(new Customer(){{
            setId(2L);
        }});
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        assertThrows(RuntimeException.class, () -> messageService.getMessageRequestDtoByMessageId(customerId, 1L));
    }

    @Test
    void getMessageRequestDtoByMessageId_overload_shouldReturnDto() {
        Message message = new Message();
        message.setId(2L);
        message.setTitle("Overloaded");
        message.setBody("Test Overloaded");
        when(messageRepository.findById(2L)).thenReturn(Optional.of(message));
        when(imageRepository.findAllByMessageId(2L)).thenReturn(Collections.emptyList());
        when(receiverRepository.findByMessageId(2L)).thenReturn(Collections.emptyList());

        MessageRequestDto dto = messageService.getMessageRequestDtoByMessageId(2L);
        assertNotNull(dto);
        assertEquals(2L, dto.getMessageId());
        assertEquals("Overloaded", dto.getTitle());
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
        when(imageRepository.findAllByMessageId(anyLong())).thenReturn(Collections.emptyList());
        when(receiverRepository.findByMessageId(anyLong())).thenReturn(Collections.emptyList());

        List<MessageRequestDto> result = messageService.getMessagesRequestDtoByCustomerId(customerId);
        assertEquals(2, result.size());
    }

    @Test
    void createMessage_shouldCreateMessageSuccessfully_withoutRecipients() {
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);
        MessageRequestDto dto = new MessageRequestDto();
        dto.setTitle("Test");
        dto.setBody("Test Body");
        dto.setRecipients(Collections.emptyList());
        dto.setCustomImages(Collections.emptyList());

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
    void createMessage_shouldProcessRecipients() {
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);
        MessageRequestDto dto = new MessageRequestDto();
        dto.setTitle("Test Recipients");
        dto.setBody("With Recipients");
        MessageRequestDto.RecipientDto rec1 = new MessageRequestDto.RecipientDto();
        rec1.setEmail("rec1@example.com");
        rec1.setTelephone("123456789");
        rec1.setName("Rec One");
        MessageRequestDto.RecipientDto rec2 = new MessageRequestDto.RecipientDto();
        rec2.setEmail("rec2@example.com");
        rec2.setTelephone("987654321");
        rec2.setName("Rec Two");
        dto.setRecipients(List.of(rec1, rec2));
        dto.setCustomImages(Collections.emptyList());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> {
            Message m = i.getArgument(0);
            m.setId(1L);
            return m;
        });
        when(aesCipher.encrypt(anyString())).thenReturn("encryptedCode");

        when(receiverService.saveReceiverByRecipientDto(any(MessageRequestDto.RecipientDto.class), any(Message.class)))
                .thenReturn(null);

        Message result = messageService.createMessage(dto, customerId);

        assertNotNull(result);
        verify(receiverService, times(2))
            .saveReceiverByRecipientDto(any(MessageRequestDto.RecipientDto.class), eq(result));
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
        dto.setCustomImages(Collections.emptyList());

        assertThrows(Exception.class, () -> messageService.updateMessage(1L, dto, 1L));
    }

    @Test
    void updateMessage_shouldRemoveObsoleteImages() {
        Message message = new Message();
        message.setId(1L);
        Customer customer = new Customer();
        customer.setId(1L);
        message.setCustomer(customer);
        message.setTitle("Old Title");
        message.setBody("Old Body");

        Image image = new Image();
        image.setImageUrl("old-url");
        image.setMessage(message);

        MessageRequestDto dto = new MessageRequestDto();
        dto.setTitle("Updated Title");
        dto.setBody("Updated Body");
        dto.setCustomImages(Collections.emptyList());
        dto.setRecipients(Collections.emptyList());

        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        when(imageRepository.findAllByMessageId(1L)).thenReturn(new ArrayList<>(List.of(image)));
        when(messageRepository.save(message)).thenReturn(message);

        Message updated = messageService.updateMessage(1L, dto, 1L);

        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Body", updated.getBody());
        verify(mediaHandler).deleteImageFromCloudinary("old-url");
        verify(imageRepository).delete(image);
    }

    @Test
void updateMessage_shouldUpdateRecipients() {
    Message message = new Message();
    message.setId(1L);
    Customer customer = new Customer();
    customer.setId(1L);
    message.setCustomer(customer);
    message.setTitle("Old Title");
    message.setBody("Old Body");

    Receiver existingReceiver = new Receiver();
    existingReceiver.setId(10L);
    existingReceiver.setTelephone("123456789");
    existingReceiver.setEmail("existing@example.com");

    List<Receiver> currentReceivers = new ArrayList<>();
    currentReceivers.add(existingReceiver);

    when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
    when(imageRepository.findAllByMessageId(1L)).thenReturn(Collections.emptyList());
    when(receiverRepository.findByMessageId(1L)).thenReturn(currentReceivers);

    MessageRequestDto dto = new MessageRequestDto();
    dto.setTitle("Updated Title");
    dto.setBody("Updated Body");
    dto.setCustomImages(Collections.emptyList());
    MessageRequestDto.RecipientDto recExisting = new MessageRequestDto.RecipientDto();
    recExisting.setTelephone("123456789");
    recExisting.setEmail("existing@example.com");
    recExisting.setName("Existing");
    MessageRequestDto.RecipientDto recNew = new MessageRequestDto.RecipientDto();
    recNew.setTelephone("987654321");
    recNew.setEmail("new@example.com");
    recNew.setName("New");
    dto.setRecipients(List.of(recExisting, recNew));

    when(receiverRepository.findByMessageIdAndTelephoneOrEmail(1L, "123456789", "existing@example.com"))
            .thenReturn(Optional.of(existingReceiver));
    when(receiverRepository.findByMessageIdAndTelephoneOrEmail(1L, "987654321", "new@example.com"))
            .thenReturn(Optional.empty());

    when(receiverService.updateMessageReceiver(existingReceiver.getId(), recExisting))
            .thenReturn(null);
    when(receiverService.saveReceiverByRecipientDto(recNew, message))
            .thenReturn(null);

    when(messageRepository.save(message)).thenReturn(message);

    Message updated = messageService.updateMessage(1L, dto, 1L);

    assertEquals("Updated Title", updated.getTitle());
    assertEquals("Updated Body", updated.getBody());
    verify(receiverService).updateMessageReceiver(existingReceiver.getId(), recExisting);
    verify(receiverService).saveReceiverByRecipientDto(recNew, message);
}


    @Test
    void deleteMessage_shouldDeleteAllAssociatedData() {
        Message message = new Message();
        message.setId(1L);
        Customer customer = new Customer();
        customer.setId(1L);
        message.setCustomer(customer);

        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));
        
        when(userService.authorizeUserOrAdmin(1L, "User not authorized to access this resource"))
                .thenReturn(null);

        Image image = new Image();
        image.setImageUrl("http://dummy.url/image.png");
        List<Image> images = List.of(image);
        when(imageRepository.findAllByMessageId(1L)).thenReturn(images);

        Receiver receiver = new Receiver();
        receiver.setId(100L);
        List<Receiver> receivers = List.of(receiver);
        when(receiverRepository.findByMessageId(1L)).thenReturn(receivers);

        when(mediaHandler.deleteImageFromCloudinary("http://dummy.url/image.png")).thenReturn("ok");
        doNothing().when(imageRepository).delete(image);
        doNothing().when(receiverRepository).delete(receiver);
        doNothing().when(messageRepository).delete(message);

        messageService.deleteMessage(1L, 1L);

        verify(userService).authorizeUserOrAdmin(1L, "User not authorized to access this resource");
        verify(mediaHandler).deleteImageFromCloudinary("http://dummy.url/image.png");
        verify(imageRepository).delete(image);
        verify(receiverRepository).delete(receiver);
        verify(messageRepository).delete(message);
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
    void testUploadNewImages_shouldProcessOnlyNewImages() throws Exception {
        Message message = new Message();
        message.setId(100L);
        Customer customer = new Customer();
        customer.setDni("X1234567Y");
        message.setCustomer(customer);
        
        Image existingImage = new Image();
        existingImage.setImageUrl("data:image/png;base64,AAA");
        List<Image> existingImages = new ArrayList<>();
        existingImages.add(existingImage);
        
        List<String> requestImageUrls = List.of("data:image/png;base64,AAA", "data:image/png;base64,BBB");
        
        when(mediaHandler.uploadImageToCloudinary(
                eq("data:image/png;base64,BBB"), 
                eq("X1234567Y/messages/100/")))
            .thenReturn("http://dummy.secure.url/BBB.png");
        
        when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        java.lang.reflect.Method method = MessageService.class.getDeclaredMethod("uploadNewImages", List.class, List.class, Message.class);
        method.setAccessible(true);
        method.invoke(messageService, requestImageUrls, existingImages, message);
        
        verify(mediaHandler, times(1))
            .uploadImageToCloudinary(eq("data:image/png;base64,BBB"), eq("X1234567Y/messages/100/"));
        
        verify(imageRepository, times(1)).save(argThat(img ->
                "http://dummy.secure.url/BBB.png".equals(img.getImageUrl()) &&
                message.equals(img.getMessage())
        ));
    }

    @Test
    void testConvertToDto() throws Exception {
        Message message = new Message();
        message.setId(123L);
        message.setTitle("Test Message");
        message.setBody("This is a test");
        message.setIsLastWill(false);

        Customer customer = new Customer();
        customer.setId(1L);
        message.setCustomer(customer);
        
        Image image1 = new Image();
        image1.setImageUrl("http://image1.jpg");
        Image image2 = new Image();
        image2.setImageUrl("http://image2.jpg");
        List<Image> images = List.of(image1, image2);
        when(imageRepository.findAllByMessageId(123L)).thenReturn(images);
        
        Receiver receiver1 = new Receiver();
        receiver1.setName("Receiver One");
        receiver1.setTelephone("111111111");
        receiver1.setEmail("r1@example.com");
        Receiver receiver2 = new Receiver();
        receiver2.setName("Receiver Two");
        receiver2.setTelephone("222222222");
        receiver2.setEmail("r2@example.com");
        List<Receiver> receivers = List.of(receiver1, receiver2);
        when(receiverRepository.findByMessageId(123L)).thenReturn(receivers);
        
        java.lang.reflect.Method method = MessageService.class.getDeclaredMethod("convertToDto", Message.class);
        method.setAccessible(true);
        MessageRequestDto dto = (MessageRequestDto) method.invoke(messageService, message);
        
        assertNotNull(dto, "El DTO no debe ser null");
        assertEquals(123L, dto.getMessageId());
        assertEquals("Test Message", dto.getTitle());
        assertEquals("This is a test", dto.getBody());
        assertEquals(false, dto.getIsLastWill());
        
        List<String> imageUrls = dto.getCustomImages();
        assertNotNull(imageUrls, "La lista de imágenes no debe ser null");
        assertEquals(2, imageUrls.size());
        assertTrue(imageUrls.contains("http://image1.jpg"), "Debe contener la URL http://image1.jpg");
        assertTrue(imageUrls.contains("http://image2.jpg"), "Debe contener la URL http://image2.jpg");
        
        List<MessageRequestDto.RecipientDto> recipientsDto = dto.getRecipients();
        assertNotNull(recipientsDto, "La lista de recipients no debe ser null");
        assertEquals(2, recipientsDto.size());
        
        boolean rec1Found = recipientsDto.stream().anyMatch(r ->
                "Receiver One".equals(r.getName()) &&
                "111111111".equals(r.getTelephone()) &&
                "r1@example.com".equals(r.getEmail()));
        assertTrue(rec1Found, "No se encontró el recipient correspondiente a Receiver One");
        
        boolean rec2Found = recipientsDto.stream().anyMatch(r ->
                "Receiver Two".equals(r.getName()) &&
                "222222222".equals(r.getTelephone()) &&
                "r2@example.com".equals(r.getEmail()));
        assertTrue(rec2Found, "No se encontró el recipient correspondiente a Receiver Two");
    }

}
