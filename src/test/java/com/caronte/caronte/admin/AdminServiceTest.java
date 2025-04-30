package com.caronte.caronte.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.caronte.caronte.admin.DTOs.CertificateResponseDTO;
import com.caronte.caronte.admin.DTOs.MessageResponseDTO;
import com.caronte.caronte.admin.DTOs.ObituaryResponseDTO;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.deathCertificate.DeathCertificateRepository;
import com.caronte.caronte.image.Image;
import com.caronte.caronte.image.ImageRepository;
import com.caronte.caronte.message.Message;
import com.caronte.caronte.message.MessageRepository;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituaryRepository;
import com.caronte.caronte.receiver.Receiver;
import com.caronte.caronte.receiver.ReceiverRepository;
import com.caronte.caronte.receiver.ReceiverService;
import com.caronte.caronte.user.UserService;

@SpringBootTest
public class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private DeathCertificateRepository deathCertificateRepository;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private MessageRepository messageRepository;

    @MockitoBean
    private ImageRepository imageRepository;

    @MockitoBean
    private ObituaryRepository obituaryRepository;

    @MockitoBean
    private ReceiverService receiverService;

    @MockitoBean
    private ReceiverRepository receiverRepository;



    private DeathCertificate cert1;
    private DeathCertificate cert2;

    private Customer customer1;
    private Customer customer2;

    private CertificateResponseDTO certDTO;

    private Message msg1;
    private Message msg2;
    private List<Message> messages;
    private Image img1;
    private Image img2;

    private Obituary obituary1;
    private Obituary obituary2;


    private DeathCertificate deathCertificate;
    private Obituary obituary;
    private Message message;
    private Receiver receiver;

    @BeforeEach
    void setUp() {

        cert1 = DeathCertificate.newDeathCertificate("url1", "12345678");
        cert2 = DeathCertificate.newDeathCertificate("url2", "87654321");
        customer1 = new Customer();
        customer1.setDni("12345678");
        customer1.setName("Juan Perez");

        customer2 = new Customer();
        customer2.setDni("87654321");
        customer2.setName("Maria Lopez");

        certDTO = new CertificateResponseDTO();
        certDTO.setId(1L);
        certDTO.setCertificateUrl("url1");
        certDTO.setDni("12345678");

        msg1 = new Message();
        msg1.setId(1L);
        msg1.setTitle("Título 1");
        msg1.setBody("Cuerpo 1");

        msg2 = new Message();
        msg2.setId(2L);
        msg2.setTitle("Título 2");
        msg2.setBody("Cuerpo 2");
        messages = Arrays.asList(msg1, msg2);

        img1 = new Image();
        img1.setId(1L);
        img1.setImageUrl("http://image1.com");

        img2 = new Image();
        img2.setId(2L);
        img2.setImageUrl("http://image2.com");


        obituary1 = new Obituary();
        obituary1.setId(1L);
        obituary1.setName("Juan Perez");
        obituary1.setFarewellMessage("Te extrañaremos");
        obituary1.setFarewellPhrase("Descansa en paz");
        obituary1.setCustomImageUrl("http://image1.com");

        obituary2 = new Obituary();
        obituary2.setId(2L);
        obituary2.setName("Maria Lopez");
        obituary2.setFarewellMessage("Siempre en nuestros corazones");
        obituary2.setFarewellPhrase("Nos vemos en el cielo");
        obituary2.setCustomImageUrl("http://image2.com");


        deathCertificate = new DeathCertificate();
        deathCertificate.setId(1L);
        deathCertificate.setIsVerified(false);

        obituary = new Obituary();
        obituary.setId(1L);
        obituary.setDeathCertificate(deathCertificate);

        message = new Message();
        message.setId(1L);
        message.setDeathCertificate(deathCertificate);

        receiver = new Receiver();
        receiver.setId(1L);
        receiver.setName("Receiver Name");
        receiver.setEmail("receiver@email.com");
        receiver.setTelephone("1234567890");

    }

    @Test
    void testGetAllPendingCertificates() {

        doNothing().when(userService).authorizeAdmin(anyString());
        List<DeathCertificate> mockCertificates = Arrays.asList(cert1, cert2);
        when(deathCertificateRepository.getAllCertificatesByIsVerified(false)).thenReturn(mockCertificates);

        when(customerRepository.findByDni("12345678")).thenReturn(Optional.of(customer1));
        when(customerRepository.findByDni("87654321")).thenReturn(Optional.of(customer2));

        List<CertificateResponseDTO> result = adminService.getAllPendingCertificates();

        assertEquals(2, result.size());
        assertEquals("12345678", result.get(0).getDni());
        assertEquals("Juan Perez", result.get(0).getName());
        assertEquals("87654321", result.get(1).getDni());
        assertEquals("Maria Lopez", result.get(1).getName());

        verify(userService).authorizeAdmin(anyString());
        verify(deathCertificateRepository).getAllCertificatesByIsVerified(false);
        verify(customerRepository, times(2)).findByDni(anyString());
    }


    @Test
    void testGetAllPendingCertificates_NullCustomer() {

        doNothing().when(userService).authorizeAdmin(anyString());

        List<DeathCertificate> mockCertificates = Arrays.asList(cert1, cert2);
        when(deathCertificateRepository.getAllCertificatesByIsVerified(false)).thenReturn(mockCertificates);
    
        when(customerRepository.findByDni(anyString())).thenReturn(Optional.empty());

        List<CertificateResponseDTO> result = adminService.getAllPendingCertificates();
    
        assertEquals(2, result.size(), "Debe devolver dos certificados");
        assertEquals("", result.get(0).getDni(), "El DNI debe ser vacío si no se encuentra el cliente");
        assertEquals("", result.get(0).getName(), "El nombre debe ser vacío si no se encuentra el cliente");
        assertEquals("", result.get(1).getDni(), "El DNI debe ser vacío si no se encuentra el cliente");
        assertEquals("", result.get(1).getName(), "El nombre debe ser vacío si no se encuentra el cliente");
    
        verify(userService).authorizeAdmin(anyString());
        verify(deathCertificateRepository).getAllCertificatesByIsVerified(false);
        verify(customerRepository, times(2)).findByDni(anyString()); 
    }

    @Test
    void testGetAllPendingCertificates_DatabaseException() {
        
        doNothing().when(userService).authorizeAdmin(anyString());

        
        when(deathCertificateRepository.getAllCertificatesByIsVerified(false))
                .thenThrow(new RuntimeException("Error en la base de datos"));

        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            adminService.getAllPendingCertificates();
        });

        assertEquals("Error en la base de datos", exception.getMessage());

        verify(userService).authorizeAdmin(anyString());
        verify(deathCertificateRepository).getAllCertificatesByIsVerified(false);
    }

        @Test
    void testGetCertificateById_Success() {
        doNothing().when(userService).authorizeAdmin(anyString());
        CertificateResponseDTO certDTO = new CertificateResponseDTO();

        when(deathCertificateRepository.getCertificateById(1L)).thenReturn(certDTO);
        CertificateResponseDTO result = adminService.getCertificateById(1L);
        assertNotNull(result);
        assertEquals(certDTO.getCertificateUrl(), result.getCertificateUrl());

        verify(userService).authorizeAdmin(anyString());
        verify(deathCertificateRepository).getCertificateById(1L);
    }

    @Test
    void testGetCertificateById_NotFound() {
        doNothing().when(userService).authorizeAdmin(anyString());
        when(deathCertificateRepository.getCertificateById(2L)).thenReturn(null);

        CertificateResponseDTO result = adminService.getCertificateById(2L);
        assertNull(result, "El resultado debe ser null cuando el certificado no existe");

        verify(userService).authorizeAdmin(anyString());
        verify(deathCertificateRepository).getCertificateById(2L);
    }



        @Test
    void testVerifyCertificate_Success() {
        DeathCertificate cert = new DeathCertificate();
        cert.setId(1L);
        cert.setIsVerified(false);

        when(deathCertificateRepository.findById(1L)).thenReturn(Optional.of(cert));

        adminService.verifyCertificate(1L);

        assertTrue(cert.getIsVerified(), "El certificado debe estar verificado");
        verify(deathCertificateRepository).save(cert);
    }

    @Test
    void testVerifyCertificate_CertificateNotFound() {
        when(deathCertificateRepository.findById(2L)).thenReturn(Optional.empty());

        adminService.verifyCertificate(2L);

        verify(deathCertificateRepository, never()).save(any());
    }

    @Test
    void testVerifyCertificate_DatabaseException() {
        when(deathCertificateRepository.findById(3L))
            .thenThrow(new RuntimeException("Error en la base de datos"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            adminService.verifyCertificate(3L);
        });

        assertEquals("Error en la base de datos", exception.getMessage());

        verify(deathCertificateRepository, never()).save(any());
    }



    @Test
    void testGetAllMessagesByCertificateId_Success() {
        doNothing().when(userService).authorizeAdmin(anyString());

        when(messageRepository.findAllByDeathCertificateId(100L)).thenReturn(messages);

        when(imageRepository.findAllByMessageId(1L)).thenReturn(List.of(img1));
        when(imageRepository.findAllByMessageId(2L)).thenReturn(List.of(img2));

        List<MessageResponseDTO> result = adminService.getAllMessagesByCertificateId(100L);

        assertEquals(2, result.size());
        assertEquals("Título 1", result.get(0).getTitle());
        assertEquals("Cuerpo 1", result.get(0).getBody());
        assertEquals(List.of("http://image1.com"), result.get(0).getImages());

        assertEquals("Título 2", result.get(1).getTitle());
        assertEquals("Cuerpo 2", result.get(1).getBody());
        assertEquals(List.of("http://image2.com"), result.get(1).getImages());

        verify(userService).authorizeAdmin(anyString());
        verify(messageRepository).findAllByDeathCertificateId(100L);
        verify(imageRepository, times(2)).findAllByMessageId(anyLong());
    }


    @Test
    void testGetAllMessagesByCertificateId_NoMessages() {
        doNothing().when(userService).authorizeAdmin(anyString());

        when(messageRepository.findAllByDeathCertificateId(200L)).thenReturn(Collections.emptyList());

        List<MessageResponseDTO> result = adminService.getAllMessagesByCertificateId(200L);

        assertTrue(result.isEmpty(), "La lista debe estar vacía si no hay mensajes");

        verify(userService).authorizeAdmin(anyString());
        verify(messageRepository).findAllByDeathCertificateId(200L);
        verify(imageRepository, never()).findAllByMessageId(anyLong());
    }

    @Test
    void testGetAllMessagesByCertificateId_MessagesWithoutImages() {

        doNothing().when(userService).authorizeAdmin(anyString());

        when(messageRepository.findAllByDeathCertificateId(300L)).thenReturn(List.of(msg1));
        when(imageRepository.findAllByMessageId(3L)).thenReturn(Collections.emptyList());

        List<MessageResponseDTO> result = adminService.getAllMessagesByCertificateId(300L);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getImages().isEmpty(), "La lista de imágenes debe estar vacía");

        verify(userService).authorizeAdmin(anyString());
        verify(messageRepository).findAllByDeathCertificateId(300L);
        verify(imageRepository).findAllByMessageId(1L);
    }

     @Test
    void testGetAllObituariesByCertificateId_Success() {
        doNothing().when(userService).authorizeAdmin(anyString());

        when(obituaryRepository.findByDeathCertificateId(100L))
                .thenReturn(Arrays.asList(obituary1, obituary2));

        List<ObituaryResponseDTO> result = adminService.getAllObituariesByCertificateId(100L);

        assertEquals(2, result.size());
        assertEquals("Juan Perez", result.get(0).getName());
        assertEquals("Te extrañaremos", result.get(0).getFarewellMessage());
        assertEquals("http://image1.com", result.get(0).getCustomImage());

        assertEquals("Maria Lopez", result.get(1).getName());
        assertEquals("Siempre en nuestros corazones", result.get(1).getFarewellMessage());
        assertEquals("http://image2.com", result.get(1).getCustomImage());

        verify(userService, times(1)).authorizeAdmin(anyString());
        verify(obituaryRepository, times(1)).findByDeathCertificateId(100L);
    }

    @Test
    void testGetAllObituariesByCertificateId_EmptyList() {
        doNothing().when(userService).authorizeAdmin(anyString());

        when(obituaryRepository.findByDeathCertificateId(200L))
                .thenReturn(Collections.emptyList());

        List<ObituaryResponseDTO> result = adminService.getAllObituariesByCertificateId(200L);

        assertEquals(0, result.size()); 

        verify(userService, times(1)).authorizeAdmin(anyString());
        verify(obituaryRepository, times(1)).findByDeathCertificateId(200L);
    }

        @Test
    void testVerificateDeathCertificate_Success() {
        
        doNothing().when(userService).authorizeAdmin(anyString());

        when(deathCertificateRepository.findById(1L)).thenReturn(Optional.of(deathCertificate));
        when(obituaryRepository.findByDeathCertificateId(1L)).thenReturn(Arrays.asList(obituary));
        when(messageRepository.findAllByDeathCertificateId(1L)).thenReturn(Arrays.asList(message));
        when(receiverService.getReceiversByObituary(obituary)).thenReturn(Arrays.asList(receiver));
        when(receiverRepository.findByMessageId(1L)).thenReturn(Arrays.asList(receiver));

        adminService.verificateDeathCertificate(1L, LocalDate.of(2024, 1, 1));

        assertTrue(deathCertificate.getIsVerified());
        assertEquals(LocalDate.of(2024, 1, 1), obituary.getDeathDate());

        verify(deathCertificateRepository).save(deathCertificate);
        verify(obituaryRepository).save(obituary);
        verify(receiverService).sendObituary(anyList(), eq(obituary));
        verify(receiverService).sendMessage(anyList(), eq(message));
    }



    @Test
    void testVerificateDeathCertificate_CertificateNotFound() {
        doNothing().when(userService).authorizeAdmin(anyString());
    
        when(deathCertificateRepository.findById(2L)).thenReturn(Optional.empty());
    
        assertThrows(NullPointerException.class, 
            () -> adminService.verificateDeathCertificate(2L, LocalDate.of(2024, 1, 1)));
    
        verify(deathCertificateRepository, never()).save(any());
        verify(obituaryRepository, never()).save(any());
        verify(receiverService, never()).sendObituary(anyList(), any());
        verify(receiverService, never()).sendMessage(anyList(), any());
    }
    

    @Test
    void testVerificateDeathCertificate_NoObituariesOrMessages() {
        doNothing().when(userService).authorizeAdmin(anyString());

        when(deathCertificateRepository.findById(3L)).thenReturn(Optional.of(deathCertificate));
        when(obituaryRepository.findByDeathCertificateId(3L)).thenReturn(Collections.emptyList());
        when(messageRepository.findAllByDeathCertificateId(3L)).thenReturn(Collections.emptyList());
        adminService.verificateDeathCertificate(3L, LocalDate.of(2024, 1, 1));

        assertTrue(deathCertificate.getIsVerified());
        verify(deathCertificateRepository).save(deathCertificate);
        verify(obituaryRepository, never()).save(any());
        verify(receiverService, never()).sendObituary(anyList(), any());
        verify(receiverService, never()).sendMessage(anyList(), any());
    }

    @Test
    void testDisapproveCertificate_Success() {
        doNothing().when(userService).authorizeAdmin(anyString());
        deathCertificate.setId(1L);
        obituary.setDeathCertificate(deathCertificate);
        message.setDeathCertificate(deathCertificate);
    
        List<Obituary> obituaries = List.of(obituary);
        List<Message> messages = List.of(message);

        when(deathCertificateRepository.findById(1L)).thenReturn(Optional.of(deathCertificate));
        when(obituaryRepository.findByDeathCertificateId(1L)).thenReturn(obituaries);
        when(messageRepository.findAllByDeathCertificateId(1L)).thenReturn(messages);
    
        adminService.disapproveCertificate(1L);
    
        verify(obituaryRepository).save(argThat(o -> o.getDeathCertificate() == null));
        verify(messageRepository).save(argThat(m -> m.getDeathCertificate() == null));
    
        verify(deathCertificateRepository).delete(deathCertificate);
    }

}
