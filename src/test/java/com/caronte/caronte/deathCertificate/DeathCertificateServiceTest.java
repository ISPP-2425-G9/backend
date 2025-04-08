package com.caronte.caronte.deathCertificate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.deathCertificate.DTOs.DeathCertificateRequestDTO;
import com.caronte.caronte.deathCertificate.DTOs.DeathCertificateWithObituaryDniDTO;
import com.caronte.caronte.message.Message;
import com.caronte.caronte.message.MessageRepository;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituaryRepository;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.util.exceptions.CertificateAssociationException;
import com.caronte.caronte.util.exceptions.ResourceNotFound;

@ExtendWith(MockitoExtension.class)
public class DeathCertificateServiceTest {

    @Mock
    private DeathCertificateRepository deathCertificateRepository;

    @Mock
    private MediaHandler mediaHandler;

    @Mock
    private ObituaryRepository obituaryRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private DeathCertificateService deathCertificateService;

    private DeathCertificateRequestDTO requestDTO;
    private DeathCertificate deathCertificate1;
    private DeathCertificate deathCertificate2;
    private Customer customer;

    @BeforeEach
    void setUp() {
        requestDTO = new DeathCertificateRequestDTO();
        requestDTO.setFile("data:image/png;base64,iVBORw0KGgoAAA...");
        requestDTO.setDni("12345678A");
        requestDTO.setIsVerificate(false);

        deathCertificate1 = new DeathCertificate();
        deathCertificate1.setUrl("https://example.com/certificate1.png");
        deathCertificate1.setIsVerified(false);

        deathCertificate2 = new DeathCertificate();
        deathCertificate2.setUrl("https://example.com/certificate2.png");
        deathCertificate2.setIsVerified(true);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Jones Smiths");
    }

    // --- Tests para createDeathCertificate ---
    @Test
    void createDeathCertificate_ValidImage_Success() {
        String uploadedUrl = "https://cloudinary.com/certificates/cert.png";
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    
        DeathCertificateRequestDTO localRequest = new DeathCertificateRequestDTO();
        localRequest.setFile("data:image/test");
        localRequest.setDni("12345678A");
    
        try (MockedStatic<MediaHandler> mediaHandlerMock = mockStatic(MediaHandler.class)) {
            mediaHandlerMock.when(() -> MediaHandler.base64ToImage(anyString())).thenReturn(image);
            when(mediaHandler.uploadImageToCloudinary(any(), eq("certificates"))).thenReturn(uploadedUrl);
            when(deathCertificateRepository.save(any(DeathCertificate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    
            DeathCertificate result = deathCertificateService.createDeathCertificate(localRequest);
    
            assertNotNull(result);
            assertEquals(uploadedUrl, result.getUrl());
            assertFalse(result.getIsVerified());
            verify(deathCertificateRepository).save(any(DeathCertificate.class));
            verify(mediaHandler, times(1)).uploadImageToCloudinary(any(), eq("certificates"));
        }
    }

    @Test
    void createDeathCertificate_InvalidImage_ThrowsException() {
        requestDTO.setFile("invalid_base64_string");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            deathCertificateService.createDeathCertificate(requestDTO);
        });
        assertEquals("The death certificate is not a valid image", exception.getMessage());
    }

    // --- Tests para checkDeathCertificate ---
    @Test
    void checkDeathCertificate_NullRequest_ThrowsException() {
        when(customerRepository.findById(eq(1L))).thenReturn(Optional.of(customer));
        assertThrows(NullPointerException.class, () -> {
            deathCertificateService.checkDeathCertificate(null, 1L);
        });
    }

    @Test
    void checkDeathCertificate_NullDni_ThrowsException() {
        requestDTO.setDni(null);
        when(customerRepository.findById(eq(1L))).thenReturn(Optional.of(customer));
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            deathCertificateService.checkDeathCertificate(requestDTO, 1L);
        });
        assertEquals("No puedes subir un certificado de defunción con tu DNI", exception.getReason());
    }

    @Test
    void checkDeathCertificate_NullFile_ThrowsException() {
        requestDTO.setFile(null);
        when(customerRepository.findById(eq(1L))).thenReturn(Optional.of(customer));
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            deathCertificateService.checkDeathCertificate(requestDTO, 1L);
        });
        assertEquals("No hay esquelas creadas asociadas a ese DNI", exception.getMessage());
    }

    @Test
    void checkDeathCertificate_CustomerNotFound_ThrowsException() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> {
            deathCertificateService.checkDeathCertificate(requestDTO, 1L);
        });
        assertEquals("Customer not found", exception.getReason());
    }

    @Test
    void checkDeathCertificate_CustomerUploadsOwnCertificate_ThrowsException() {
        Customer loggedCustomer = new Customer();
        loggedCustomer.setDni("12345678A");
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(loggedCustomer));
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            deathCertificateService.checkDeathCertificate(requestDTO, 1L);
        });
        assertEquals("No puedes subir un certificado de defunción con tu DNI", exception.getReason());
    }

    @Test
    void checkDeathCertificate_NoObituaries_ThrowsException() {
        Customer loggedCustomer = mock(Customer.class);
        when(loggedCustomer.getDni()).thenReturn("12345678Z");
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(loggedCustomer));
        when(obituaryRepository.findByCustomerDni(anyString())).thenReturn(Collections.emptyList());
        requestDTO.setDni("12345678Z");
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            deathCertificateService.checkDeathCertificate(requestDTO, 1L);
        });
        assertEquals("No hay esquelas creadas asociadas a ese DNI", exception.getMessage());
    }

    @Test
    void checkDeathCertificate_CertificateAlreadyUploaded_ThrowsException() {
        Obituary mockObituary = mock(Obituary.class);
        Customer loggedCustomer = mock(Customer.class);
        DeathCertificateRequestDTO mockRequest = mock(DeathCertificateRequestDTO.class);
        String dni = "12345678K";
        when(loggedCustomer.getDni()).thenReturn(dni); 
        when(mockRequest.getDni()).thenReturn(dni);  
        when(mockObituary.getDeathCertificate()).thenReturn(deathCertificate1); 
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(loggedCustomer));
        when(obituaryRepository.findByCustomerDni(eq(dni))).thenReturn(Collections.singletonList(mockObituary));  
        
        CertificateAssociationException exception = 
            assertThrows(CertificateAssociationException.class, () -> {
                deathCertificateService.checkDeathCertificate(mockRequest, 1L);  
            });
        assertEquals("El certificado de este cliente ya ha sido subido", exception.getMessage());
    }

    @Test
    void createDeathCertificateAndRelations_Success() {
        DeathCertificateRequestDTO localRequest = new DeathCertificateRequestDTO();
        localRequest.setFile("data:image/png;base64,validdata");
        localRequest.setDni("12345678A");
        localRequest.setIsVerificate(false);

        Customer loggedCustomer = new Customer();
        loggedCustomer.setId(1L);
        loggedCustomer.setDni("11111111X");
        when(customerRepository.findById(eq(1L))).thenReturn(Optional.of(loggedCustomer));
        
        Obituary obituaryForLogged = new Obituary();
        obituaryForLogged.setIsMine(true);
        obituaryForLogged.setDeathCertificate(null);
        when(obituaryRepository.findByCustomerDni(eq(loggedCustomer.getDni())))
            .thenReturn(Arrays.asList(obituaryForLogged));
        
        try (MockedStatic<MediaHandler> mediaHandlerStatic = mockStatic(MediaHandler.class)) {
            BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            mediaHandlerStatic.when(() -> MediaHandler.base64ToImage(anyString())).thenReturn(image);
            String uploadedUrl = "https://cloudinary.com/certificates/new_cert.png";
            when(mediaHandler.uploadImageToCloudinary(any(), eq("certificates"))).thenReturn(uploadedUrl);
            when(deathCertificateRepository.save(any(DeathCertificate.class)))
                .thenAnswer(invocation -> {
                    DeathCertificate dc = invocation.getArgument(0);
                    dc.setIsVerified(false);
                    return dc;
                });
            
            Obituary obituaryForRequest = new Obituary();
            obituaryForRequest.setIsMine(true);
            obituaryForRequest.setDeathCertificate(null);
            when(obituaryRepository.findByCustomerDni(eq(localRequest.getDni())))
                .thenReturn(Arrays.asList(obituaryForRequest));
            
            Customer customerForRelation = new Customer();
            customerForRelation.setId(2L);
            customerForRelation.setDni(localRequest.getDni());
            when(customerRepository.findByDni(eq(localRequest.getDni()))).thenReturn(Optional.of(customerForRelation));
            
            Message message1 = new Message();
            Message message2 = new Message();
            when(messageRepository.findAllByCustomerId(eq(customerForRelation.getId())))
                .thenReturn(Arrays.asList(message1, message2));
            when(messageRepository.saveAll(anyList())).thenReturn(null);

            DeathCertificate certificateResult = deathCertificateService.createDeathCertificateAndRelations(localRequest, 1L);
            
            assertNotNull(certificateResult);
            assertEquals(uploadedUrl, certificateResult.getUrl());
            
            verify(obituaryRepository).saveAll(argThat(obits -> 
                StreamSupport.stream(obits.spliterator(), false)
                    .allMatch(o -> o.getDeathCertificate() == certificateResult)
            ));
            verify(messageRepository).saveAll(argThat(msgs -> 
                StreamSupport.stream(msgs.spliterator(), false)
                    .allMatch(m -> m.getDeathCertificate() == certificateResult)
            ));
        }
    }
    
    @Test
    void createDeathCertificateAndRelations_NullCustomerId_Success() {
        DeathCertificateRequestDTO localRequest = new DeathCertificateRequestDTO();
        localRequest.setFile("data:image/png;base64,validdata");
        localRequest.setDni("12345678A");
        localRequest.setIsVerificate(false);
        
        Obituary obituaryForRequest = new Obituary();
        obituaryForRequest.setIsMine(false);
        when(obituaryRepository.findByCustomerDni(eq(localRequest.getDni())))
            .thenReturn(Arrays.asList(obituaryForRequest));
        
        try (MockedStatic<MediaHandler> mediaHandlerStatic = mockStatic(MediaHandler.class)) {
            BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            mediaHandlerStatic.when(() -> MediaHandler.base64ToImage(anyString())).thenReturn(image);
            String uploadedUrl = "https://cloudinary.com/certificates/new_cert2.png";
            when(mediaHandler.uploadImageToCloudinary(any(), eq("certificates"))).thenReturn(uploadedUrl);
            when(deathCertificateRepository.save(any(DeathCertificate.class)))
                .thenAnswer(invocation -> {
                    DeathCertificate dc = invocation.getArgument(0);
                    dc.setIsVerified(false);
                    return dc;
                });
            
            Customer customerForRelation = new Customer();
            customerForRelation.setId(3L);
            customerForRelation.setDni(localRequest.getDni());
            when(customerRepository.findByDni(eq(localRequest.getDni()))).thenReturn(Optional.of(customerForRelation));
            
            Message message = new Message();
            when(messageRepository.findAllByCustomerId(eq(customerForRelation.getId())))
                .thenReturn(Arrays.asList(message));
            when(messageRepository.saveAll(anyList())).thenReturn(null);
            
            DeathCertificate certificateResult = deathCertificateService.createDeathCertificateAndRelations(localRequest, null);
            
            assertNotNull(certificateResult);
            assertEquals(uploadedUrl, certificateResult.getUrl());
            verify(obituaryRepository).findByCustomerDni(eq(localRequest.getDni()));
            verify(messageRepository).findAllByCustomerId(eq(customerForRelation.getId()));
        }
    }

    @Test
    void getObituariesByDni_Success() {
        String dni = "12345678A";
        Obituary obituary1 = new Obituary();
        Obituary obituary2 = new Obituary();
        List<Obituary> obituaryList = Arrays.asList(obituary1, obituary2);
        when(obituaryRepository.findByCustomerDni(eq(dni))).thenReturn(obituaryList);

        List<Obituary> result = deathCertificateService.getObituariesByDni(dni);
        assertEquals(2, result.size());
        assertEquals(obituaryList, result);
    }

    @Test
    void getObituariesByDni_Empty_ThrowsException() {
        String dni = "12345678A";
        when(obituaryRepository.findByCustomerDni(eq(dni))).thenReturn(Collections.emptyList());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            deathCertificateService.getObituariesByDni(dni);
        });
        assertEquals("No hay esquelas creadas asociadas a ese DNI", exception.getMessage());
    }
    
    @Test
    void getDeathCertificateByObituaryId_Success() {
        DeathCertificate certificate = new DeathCertificate();
        certificate.setUrl("https://example.com/certificate.png");
        Obituary obituary = mock(Obituary.class);
        Customer obituaryCustomer = mock(Customer.class);
        when(obituaryCustomer.getDni()).thenReturn("12345678A");
        when(obituary.getCustomer()).thenReturn(obituaryCustomer);
        when(obituary.getDeathCertificate()).thenReturn(certificate);
        when(obituaryRepository.findById(anyLong())).thenReturn(Optional.of(obituary));
        
        DeathCertificateWithObituaryDniDTO result = deathCertificateService.getDeathCertificateByObituaryId(1L);
        
        assertNotNull(result);
        assertEquals("12345678A", result.getDni());
        assertEquals("https://example.com/certificate.png", result.getDeathCertificate().getUrl());
    }

    @Test
    void getDeathCertificateByObituaryId_ObituaryNotFound_ThrowsException() {
        when(obituaryRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            deathCertificateService.getDeathCertificateByObituaryId(1L);
        });
        assertEquals("Obituary not found", exception.getReason());
    }

    @Test
    void getDeathCertificateByObituaryId_NoDeathCertificate_ThrowsException() {
        Obituary obituary = mock(Obituary.class);
        when(obituaryRepository.findById(eq(1L))).thenReturn(Optional.of(obituary));
        
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> {
            deathCertificateService.getDeathCertificateByObituaryId(1L);
        });
        assertEquals("Death certificate not found", exception.getReason());
    }

    @Test
    void testGetAllDeathCertificates() {
        List<DeathCertificate> deathCertificates = Arrays.asList(deathCertificate1, deathCertificate2);
        when(deathCertificateRepository.findAll()).thenReturn(deathCertificates);

        List<DeathCertificate> result = deathCertificateService.getAllDeathCertificates();
        assertEquals(2, result.size());  
        assertEquals(deathCertificate1.getUrl(), result.get(0).getUrl()); 
        assertEquals(deathCertificate2.getUrl(), result.get(1).getUrl()); 
    }
}
