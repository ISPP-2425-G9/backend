package com.caronte.caronte.deathCertificate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.deathCertificate.DTOs.DeathCertificateRequestDTO;
import com.caronte.caronte.deathCertificate.DTOs.DeathCertificateWithObituaryDniDTO;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituaryRepository;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.util.exceptions.CertificateAssociationException;
import com.caronte.caronte.util.exceptions.ResourceNotFound;

@SpringBootTest
public class DeathCertificateServiceTest {

    @MockitoBean
    private DeathCertificateRepository deathCertificateRepository;

    @MockitoBean
    private MediaHandler mediaHandler;

    @MockitoBean
    private ObituaryRepository obituaryRepository;

    @MockitoBean
    private CustomerRepository customerRepository;

    @Autowired
    private DeathCertificateService deathCertificateService;

    private DeathCertificateRequestDTO requestDTO;
    private DeathCertificate deathCertificate1;
    private DeathCertificate deathCertificate2;
    
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


    }

    @Test
    void createDeathCertificate_ValidImage_Success() {
        String uploadedUrl = "https://cloudinary.com/certificates/cert.png";
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        try (var mediaHandlerMock = mockStatic(MediaHandler.class)) {
            mediaHandlerMock.when(() -> mediaHandler.base64ToImage(anyString())).thenReturn(image);
            mediaHandlerMock.when(() -> mediaHandler.uploadImageToCloudinary(any(), eq("certificates"))).thenReturn(uploadedUrl);
            when(deathCertificateRepository.save(any(DeathCertificate.class))).thenAnswer(invocation -> invocation.getArgument(0));

            DeathCertificate result = deathCertificateService.createDeathCertificate(requestDTO);

            assertNotNull(result);
            assertEquals(uploadedUrl, result.getUrl());
            assertFalse(result.getIsVerified());
            verify(deathCertificateRepository).save(any(DeathCertificate.class));
            mediaHandlerMock.verify(() -> mediaHandler.base64ToImage(anyString()), times(1));
            mediaHandlerMock.verify(() -> mediaHandler.uploadImageToCloudinary(any(), eq("certificates")), times(1));
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
     @Test
    void checkDeathCertificate_NullRequest_ThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            deathCertificateService.checkDeathCertificate(null, 1L);
        });
        assertEquals("The Death Certificate is invalid", exception.getMessage());
    }

    @Test
    void checkDeathCertificate_NullDni_ThrowsException() {
        requestDTO.setDni(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            deathCertificateService.checkDeathCertificate(requestDTO, 1L);
        });
        assertEquals("The Death Certificate is invalid", exception.getMessage());
    }

    @Test
    void checkDeathCertificate_NullFile_ThrowsException() {
        requestDTO.setFile(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            deathCertificateService.checkDeathCertificate(requestDTO, 1L);
        });
        assertEquals("The Death Certificate is invalid", exception.getMessage());
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
        Customer customer = new Customer();
        customer.setDni("12345678A");
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            deathCertificateService.checkDeathCertificate(requestDTO, 1L);
        });
        assertEquals("No puedes subir un certificado de defunción con tu DNI", exception.getReason());
    }

    @Test
    void checkDeathCertificate_NoObituaries_ThrowsException() {
        when(obituaryRepository.findByCustomerDni(anyString())).thenReturn(Collections.emptyList());
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            deathCertificateService.checkDeathCertificate(requestDTO, null);
        });
        assertEquals("No hay esquelas creadas asociadas a ese DNI", exception.getMessage());
    }

    @Test
    void checkDeathCertificate_CertificateAlreadyUploaded_ThrowsException() {
        Obituary obituary = mock(Obituary.class);
        when(obituary.getDeathCertificate()).thenReturn(new DeathCertificate());
        when(obituaryRepository.findByCustomerDni(anyString())).thenReturn(Collections.singletonList(obituary));
        
        CertificateAssociationException exception = assertThrows(CertificateAssociationException.class, () -> {
            deathCertificateService.checkDeathCertificate(requestDTO, null);
        });
        assertEquals("El certificado de este cliente ya ha sido subido", exception.getMessage());
    }

    @Test
    void getDeathCertificateByObituaryId_Success() {
        DeathCertificate certificate = new DeathCertificate();
        certificate.setUrl("https://example.com/certificate.png");
        Obituary obituary = mock(Obituary.class);
        Customer customer = mock(Customer.class);
        when(customer.getDni()).thenReturn("12345678A");
        when(obituary.getCustomer()).thenReturn(customer);
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
        when(obituary.getDeathCertificate()).thenReturn(null);
        when(obituaryRepository.findById(anyLong())).thenReturn(Optional.of(obituary));
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            deathCertificateService.getDeathCertificateByObituaryId(1L);
        });
        assertEquals("Death certificate not found", exception.getReason());
    }


    @Test
    void testGetAllDeathCertificates() {

        List<DeathCertificate> deathCertificates = Arrays.asList(deathCertificate1, deathCertificate2);
        when(deathCertificateRepository.findAll()).thenReturn(deathCertificates);

        Iterable<DeathCertificate> result = deathCertificateService.getAllDeathCertificates();
        List<DeathCertificate> list = StreamSupport.stream(result.spliterator(), false)
                                             .collect(Collectors.toList());
        assertEquals(list.size(), 2);  
        assertEquals(list.get(0).getUrl(), deathCertificate1.getUrl()); 
        assertEquals(list.get(1).getUrl(), deathCertificate2.getUrl()); 
    }

    
    
}
