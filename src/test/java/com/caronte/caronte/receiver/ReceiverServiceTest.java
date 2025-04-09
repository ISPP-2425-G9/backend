package com.caronte.caronte.receiver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.caronte.caronte.configuration.services.EmailService;
import com.caronte.caronte.message.Message;
import com.caronte.caronte.message.DTOs.MessageRequestDto.RecipientDto;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.DTOs.ObituraryRequestDto.ContactDto;
import com.caronte.caronte.receiver.DTOs.ReceiverResponseDTO;
import com.caronte.caronte.util.AESCipher;

@ExtendWith(MockitoExtension.class)
public class ReceiverServiceTest {

    @Mock
    private ReceiverRepository receiverRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private AESCipher aesCipher;

    @InjectMocks
    private ReceiverService receiverService;

    private final String dummyDomain = "http://example.com";

    private Obituary dummyObituary;
    private Message dummyMessage;

    @BeforeEach
    void setUp() throws Exception {
        Field domainField = ReceiverService.class.getDeclaredField("domain");
        domainField.setAccessible(true);
        domainField.set(receiverService, dummyDomain);

        dummyObituary = new Obituary();
        dummyObituary.setId(1L);

        dummyMessage = new Message();
        dummyMessage.setId(100L);
        com.caronte.caronte.customer.Customer dummyCustomer = new com.caronte.caronte.customer.Customer();
        dummyCustomer.setId(10L);
        dummyCustomer.setDni("12345678A");
        dummyMessage.setCustomer(dummyCustomer);
    }

    @Test
    void testGetReceiversByObituaryId() {
        com.caronte.caronte.receiver.Receiver rec1 = new com.caronte.caronte.receiver.Receiver();
        rec1.setName("John Doe");
        rec1.setTelephone("123456789");
        rec1.setEmail("john@example.com");

        com.caronte.caronte.receiver.Receiver rec2 = new com.caronte.caronte.receiver.Receiver();
        rec2.setName("Jane Doe");
        rec2.setTelephone("987654321");
        rec2.setEmail("jane@example.com");

        List<com.caronte.caronte.receiver.Receiver> list = Arrays.asList(rec1, rec2);
        when(receiverRepository.findByObituary(dummyObituary)).thenReturn(list);

        List<ReceiverResponseDTO> result = receiverService.getReceiversByObituaryId(dummyObituary);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("123456789", result.get(0).getTelephone());
        assertEquals("john@example.com", result.get(0).getEmail());
        assertEquals("Jane Doe", result.get(1).getName());
        assertEquals("987654321", result.get(1).getTelephone());
        assertEquals("jane@example.com", result.get(1).getEmail());
    }

    @Test
    void testGetReceiversByObituary() {
        List<com.caronte.caronte.receiver.Receiver> list = Collections.singletonList(new com.caronte.caronte.receiver.Receiver());
        when(receiverRepository.findByObituary(dummyObituary)).thenReturn(list);

        List<com.caronte.caronte.receiver.Receiver> result = receiverService.getReceiversByObituary(dummyObituary);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSaveObituaryReceiver() {
        ContactDto contact = new ContactDto();
        contact.setName("Alice");
        contact.setPhone("111222333");
        contact.setEmail("alice@example.com");

        com.caronte.caronte.receiver.Receiver dummyReceiver = new com.caronte.caronte.receiver.Receiver();
        dummyReceiver.setName(contact.getName());
        dummyReceiver.setTelephone(contact.getPhone());
        dummyReceiver.setEmail(contact.getEmail());
        dummyReceiver.setObituary(dummyObituary);

        when(receiverRepository.save(any(com.caronte.caronte.receiver.Receiver.class)))
                .thenReturn(dummyReceiver);

        com.caronte.caronte.receiver.Receiver result = receiverService.saveObituaryReceiver(contact, dummyObituary);
        assertNotNull(result);
        assertEquals("Alice", result.getName());
        assertEquals("111222333", result.getTelephone());
        assertEquals("alice@example.com", result.getEmail());
        assertEquals(dummyObituary, result.getObituary());
    }

    @Test
    void testSaveReceiverByRecipientDto() {
        RecipientDto recipient = new RecipientDto();
        recipient.setName("Bob");
        recipient.setTelephone("444555666");
        recipient.setEmail("bob@example.com");

        com.caronte.caronte.receiver.Receiver dummyReceiver = new com.caronte.caronte.receiver.Receiver();
        dummyReceiver.setName(recipient.getName());
        dummyReceiver.setTelephone(recipient.getTelephone());
        dummyReceiver.setEmail(recipient.getEmail());
        dummyReceiver.setMessage(dummyMessage);

        when(receiverRepository.save(any(com.caronte.caronte.receiver.Receiver.class)))
                .thenReturn(dummyReceiver);

        com.caronte.caronte.receiver.Receiver result = receiverService.saveReceiverByRecipientDto(recipient, dummyMessage);
        assertNotNull(result);
        assertEquals("Bob", result.getName());
        assertEquals("444555666", result.getTelephone());
        assertEquals("bob@example.com", result.getEmail());
        assertEquals(dummyMessage, result.getMessage());
    }

    @Test
    void testSaveAllObituaryReceiver() {
        ContactDto contact1 = new ContactDto();
        contact1.setName("Alice");
        contact1.setPhone("111222333");
        contact1.setEmail("alice@example.com");

        ContactDto contact2 = new ContactDto();
        contact2.setName("Bob");
        contact2.setPhone("444555666");
        contact2.setEmail("bob@example.com");

        List<ContactDto> contacts = Arrays.asList(contact1, contact2);
        com.caronte.caronte.receiver.Receiver r1 = new com.caronte.caronte.receiver.Receiver();
        r1.setName("Alice");
        r1.setTelephone("111222333");
        r1.setEmail("alice@example.com");
        r1.setObituary(dummyObituary);
        com.caronte.caronte.receiver.Receiver r2 = new com.caronte.caronte.receiver.Receiver();
        r2.setName("Bob");
        r2.setTelephone("444555666");
        r2.setEmail("bob@example.com");
        r2.setObituary(dummyObituary);

        List<com.caronte.caronte.receiver.Receiver> receiversList = Arrays.asList(r1, r2);
        when(receiverRepository.saveAll(anyList())).thenReturn(receiversList);

        List<com.caronte.caronte.receiver.Receiver> result = receiverService.saveAllObituaryReceiver(contacts, dummyObituary);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testDeleteReceiversByObituaryId() {
        receiverService.deleteReceiversByObituaryId(dummyObituary);
        verify(receiverRepository, times(1)).deleteByObituary(dummyObituary);
        verify(receiverRepository, times(1)).flush();
    }

    @Test
    void testUpdateMessageReceiver() {
        com.caronte.caronte.receiver.Receiver existingReceiver = new com.caronte.caronte.receiver.Receiver();
        existingReceiver.setId(50L);
        existingReceiver.setName("Old Name");
        existingReceiver.setTelephone("000000000");
        existingReceiver.setEmail("old@example.com");

        RecipientDto recipient = new RecipientDto();
        recipient.setName("New Name");
        recipient.setTelephone("999999999");
        recipient.setEmail("new@example.com");

        when(receiverRepository.findById(50L)).thenReturn(Optional.of(existingReceiver));
        when(receiverRepository.save(existingReceiver)).thenReturn(existingReceiver);

        com.caronte.caronte.receiver.Receiver updated = receiverService.updateMessageReceiver(50L, recipient);
        assertNotNull(updated);
        assertEquals("New Name", updated.getName());
        assertEquals("999999999", updated.getTelephone());
        assertEquals("new@example.com", updated.getEmail());
    }

    @Test
    void testSendObituary() throws Exception {
        com.caronte.caronte.receiver.Receiver r1 = new com.caronte.caronte.receiver.Receiver();
        r1.setEmail("r1@example.com");
        com.caronte.caronte.receiver.Receiver r2 = new com.caronte.caronte.receiver.Receiver();
        r2.setEmail("r2@example.com");
        List<com.caronte.caronte.receiver.Receiver> receivers = Arrays.asList(r1, r2);

        Obituary dummyObituary = new Obituary();
        dummyObituary.setId(1L);
        dummyObituary.setName("ObitName");

        byte[] pdfBytes = "dummy-pdf".getBytes();
        when(emailService.generateObituaryPdf(dummyObituary)).thenReturn(pdfBytes);

        doNothing().when(emailService).sendEmailWithAttachment(
            eq("r1@example.com"),
            eq("Esquela de ObitName"),
            eq("Adjunto encontrarás la esquela de ObitName"),
            eq(pdfBytes),
            eq("esquela_ObitName.pdf")
        );
        doThrow(new RuntimeException("Error email")).when(emailService).sendEmailWithAttachment(
            eq("r2@example.com"),
            eq("Esquela de ObitName"),
            eq("Adjunto encontrarás la esquela de ObitName"),
            eq(pdfBytes),
            eq("esquela_ObitName.pdf")
        );

        receiverService.sendObituary(receivers, dummyObituary);

        verify(emailService).sendEmailWithAttachment(
            eq("r1@example.com"),
            eq("Esquela de ObitName"),
            eq("Adjunto encontrarás la esquela de ObitName"),
            eq(pdfBytes),
            eq("esquela_ObitName.pdf")
        );
        verify(emailService).sendEmailWithAttachment(
            eq("r2@example.com"),
            eq("Esquela de ObitName"),
            eq("Adjunto encontrarás la esquela de ObitName"),
            eq(pdfBytes),
            eq("esquela_ObitName.pdf")
        );
    }

    @Test
    void testSendMessage() throws Exception {
        com.caronte.caronte.receiver.Receiver r1 = new com.caronte.caronte.receiver.Receiver();
        r1.setEmail("r1@example.com");
        com.caronte.caronte.receiver.Receiver r2 = new com.caronte.caronte.receiver.Receiver();
        r2.setEmail("r2@example.com");
        List<com.caronte.caronte.receiver.Receiver> receivers = Arrays.asList(r1, r2);

        Message dummyMessage = new Message();
        dummyMessage.setId(200L);
        dummyMessage.setCode("encryptedCode");
        when(aesCipher.decrypt("encryptedCode")).thenReturn("12345");

        doNothing().when(emailService).sendEmail(eq("r1@example.com"), anyString(), anyString());
        doNothing().when(emailService).sendEmail(eq("r2@example.com"), anyString(), anyString());

        com.caronte.caronte.customer.Customer cust = new com.caronte.caronte.customer.Customer();
        cust.setName("CustomerName");
        dummyMessage.setCustomer(cust);

        receiverService.sendMessage(receivers, dummyMessage);

        verify(emailService).sendEmail(eq("r1@example.com"), anyString(), contains("12345"));
        verify(emailService).sendEmail(eq("r2@example.com"), anyString(), contains("12345"));
    }

    @Test
    void testSaveMessageReceiver() {
        String name = "Receiver X";
        String telephone = "555666777";
        String email = "rx@example.com";

        Message dummyMsg = new Message();
        dummyMsg.setId(300L);

        com.caronte.caronte.receiver.Receiver dummyReceiver = new com.caronte.caronte.receiver.Receiver();
        dummyReceiver.setName(name);
        dummyReceiver.setTelephone(telephone);
        dummyReceiver.setEmail(email);
        dummyReceiver.setMessage(dummyMsg);

        when(receiverRepository.save(any(com.caronte.caronte.receiver.Receiver.class))).thenReturn(dummyReceiver);

        com.caronte.caronte.receiver.Receiver result = receiverService.saveMessageReceiver(name, telephone, email, dummyMsg);
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(telephone, result.getTelephone());
        assertEquals(email, result.getEmail());
        assertEquals(dummyMsg, result.getMessage());
    }
}
