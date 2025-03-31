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
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @Test
    void createDeathCertificate_ValidImage_Success() {
        String uploadedUrl = "https://cloudinary.com/certificates/cert.png";
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    
        DeathCertificateRequestDTO requestDTO = new DeathCertificateRequestDTO();
        requestDTO.setFile("someBase64EncodedString");
    
        try (var mediaHandlerMock = mockStatic(MediaHandler.class)) {
            mediaHandlerMock.when(() -> MediaHandler.base64ToImage(anyString())).thenReturn(image);
            when(mediaHandler.uploadImageToCloudinary(any(), eq("certificates"))).thenReturn(uploadedUrl);
            when(deathCertificateRepository.save(any(DeathCertificate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    
            DeathCertificate result = deathCertificateService.createDeathCertificate(requestDTO);
    
            assertNotNull(result);
            assertEquals(uploadedUrl, result.getUrl());
            assertFalse(result.getIsVerified());
            verify(deathCertificateRepository).save(any(DeathCertificate.class));
    
            mediaHandlerMock.verify(() -> MediaHandler.base64ToImage(anyString()), times(1));
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
        Customer mockCustomer = mock(Customer.class);
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(mockCustomer));
        when(obituaryRepository.findByCustomerDni(anyString())).thenReturn(Collections.emptyList());
        
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            deathCertificateService.checkDeathCertificate(requestDTO, anyLong());
        });
        assertEquals("No hay esquelas creadas asociadas a ese DNI", exception.getMessage());
    }

    @Test
    void checkDeathCertificate_CertificateAlreadyUploaded_ThrowsException() {
        Obituary obituary = mock(Obituary.class);
        String dni = "12345678K";
        Customer customer = mock(Customer.class);
        DeathCertificateRequestDTO request = mock(DeathCertificateRequestDTO.class);
        
        when(customer.getDni()).thenReturn(dni); 
        when(request.getDni()).thenReturn(dni);  
        when(obituary.getDeathCertificate()).thenReturn(any(DeathCertificate.class)); 
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));  
        when(obituaryRepository.findByCustomerDni(dni)).thenReturn(Collections.singletonList(obituary));  
        
        CertificateAssociationException exception = 
            assertThrows(CertificateAssociationException.class, () -> {
                deathCertificateService.checkDeathCertificate(request, 1L);  
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
        
        assertEquals(result.size(), 2);  
        assertEquals(result.get(0).getUrl(), deathCertificate1.getUrl()); 
        assertEquals(result.get(1).getUrl(), deathCertificate2.getUrl()); 
    }

}
