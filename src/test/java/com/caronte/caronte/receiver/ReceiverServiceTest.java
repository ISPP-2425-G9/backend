package com.caronte.caronte.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.caronte.caronte.message.Message;
import com.caronte.caronte.message.DTOs.MessageRequestDto.RecipientDto;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.DTOs.ObituraryRequestDto.ContactDto;
import com.caronte.caronte.receiver.DTOs.ReceiverResponseDTO;

@ExtendWith(MockitoExtension.class)
public class ReceiverServiceTest {

    @Mock
    private ReceiverRepository receiverRepository;

    @InjectMocks
    private ReceiverService receiverService;

    private Obituary obituary;
    private Receiver receiver1, receiver2;
    private Message message;
    private ContactDto contactDto;
    private RecipientDto recipientDto;

    @BeforeEach
    void setUp() {
        obituary = new Obituary();
        obituary.setId(1L);
        message = new Message();
        message.setId(1L);

        receiver1 = new Receiver();
        receiver1.setName("John Doe");
        receiver1.setTelephone("123456789");
        receiver1.setEmail("john@example.com");
        receiver1.setMessage(message);
        receiver1.setObituary(obituary);

        receiver2 = new Receiver();
        receiver2.setName("Jane Doe");
        receiver2.setTelephone("987654321");
        receiver2.setEmail("jane@example.com");
        receiver2.setMessage(message);
        receiver2.setObituary(obituary);

        recipientDto = new RecipientDto();
        recipientDto.setName(receiver1.getName());
        recipientDto.setTelephone(receiver1.getTelephone());
        recipientDto.setEmail(receiver1.getEmail());

        contactDto = new ContactDto();
        contactDto.setName("John Doe");
        contactDto.setPhone("123456789");
        contactDto.setEmail("john@example.com");
    }

    @Test
    void testGetReceiversByObituaryId() {
        // Depuración: Verificar que receiverRepository no es null
        System.out.println("Mocked Repository: " + receiverRepository);

        when(receiverRepository.findByObituary(obituary))
            .thenReturn(Arrays.asList(receiver1, receiver2));

        List<ReceiverResponseDTO> result = receiverService.getReceiversByObituaryId(obituary);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("John Doe", result.get(0).getName());
        assertEquals("123456789", result.get(0).getTelephone());
        assertEquals("john@example.com", result.get(0).getEmail());

        assertEquals("Jane Doe", result.get(1).getName());
        assertEquals("987654321", result.get(1).getTelephone());
        assertEquals("jane@example.com", result.get(1).getEmail());

        verify(receiverRepository, times(1)).findByObituary(obituary);
    }

    @Test
    void testDeleteReceiversByObituaryId() {
        receiverService.deleteReceiversByObituaryId(obituary);
        verify(receiverRepository, times(1)).deleteByObituary(obituary);
        verify(receiverRepository, times(1)).flush();
    }

    @Test
    void testSaveObituaryReceiver() {
        when(receiverRepository.save(any(Receiver.class))).thenReturn(receiver1);

        Receiver savedReceiver = receiverService.saveObituaryReceiver(contactDto, obituary);

        assertNotNull(savedReceiver);
        assertEquals("John Doe", savedReceiver.getName());
        assertEquals("123456789", savedReceiver.getTelephone());
        assertEquals("john@example.com", savedReceiver.getEmail());
        assertEquals(obituary, savedReceiver.getObituary());

        verify(receiverRepository, times(1)).save(any(Receiver.class));
    }

    @Test
    void testSaveMessageReceiver() {
        when(receiverRepository.save(any(Receiver.class))).thenReturn(receiver1);

        Receiver savedReceiver = receiverService.saveMessageReceiver(recipientDto, message);

        assertNotNull(savedReceiver);
        assertEquals("John Doe", savedReceiver.getName());
        assertEquals("123456789", savedReceiver.getTelephone());
        assertEquals("john@example.com", savedReceiver.getEmail());
        assertEquals(message, savedReceiver.getMessage());

        verify(receiverRepository, times(1)).save(any(Receiver.class));
    }
}
