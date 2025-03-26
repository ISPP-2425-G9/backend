package com.caronte.caronte.obituary;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.imageTemplate.ImageTemplate;
import com.caronte.caronte.imageTemplate.ImageTemplateService;
import com.caronte.caronte.obituary.ObituraryRequestDto.ContactDto;
import com.caronte.caronte.receiver.ReceiverService;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.deathCertificate.DeathCertificateService;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

public class ObituaryServiceTest {

    @Mock
    private ObituaryRepository obituaryRepository;
    
    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private ImageTemplateService imageTemplateService;
    
    @Mock
    private ReceiverService receiverService;
    
    @Mock
    private DeathCertificateService deathCertificateService;
    
    @Mock
    private MediaHandler mediaHandler;

    @InjectMocks
    private ObituaryService obituaryService;

    private ObituraryRequestDto requestDto;
    private Customer customer;
    private ImageTemplate imageTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alfonso Manuel Giraldillo");
        customer.setEmail("alfonso@example.com");

        imageTemplate = new ImageTemplate();
        imageTemplate.setId(1L);

        requestDto = new ObituraryRequestDto();
        requestDto.setName("John Doe");
        requestDto.setFarewellMessage("Goodbye!");
        requestDto.setFarewellPhrase("Rest in peace.");
        requestDto.setIsMine(true);
        requestDto.setImageTemplate_id(1L);
        requestDto.setCustomImage("https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg");  
    }
@Test
    public void testCreateObituaryWithReceivers() {
        String base64Image = "https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg";
    
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(imageTemplateService.findById(1L)).thenReturn(imageTemplate); 
    
        ObituraryRequestDto requestDto = new ObituraryRequestDto();
        requestDto.setName("Alfonso Manuel Giraldillo");
        requestDto.setFarewellMessage("Esto es un mensaje de despedida");
        requestDto.setFarewellPhrase("Esto es una frase de despedida");
        requestDto.setImageTemplate_id(1L);
        requestDto.setIsMine(true);
        requestDto.setCustomImage(base64Image);  
        requestDto.setBirthDate(null);
        requestDto.setDeathDate(null);
    
        List<ContactDto> contacts = new ArrayList<>();
        ContactDto contact1 = new ContactDto();
        contact1.setName("Contacto1Nombre");
        contact1.setPhone("68556790743");
        contact1.setEmail("email1@gmail.com");
        contacts.add(contact1);
        requestDto.setContacts(contacts);
        
    
        Obituary obituary_test = obituaryService.createObituaryWithReceivers(requestDto, customer.getId());
        //when(receiverService.saveObituaryReceiver(eq("Contacto1Nombre"), eq("68556790743"), eq("email1@gmail.com"), any(Obituary.class))).thenReturn(null); // Cambiado a when()
        when(receiverService.saveObituaryReceiver(eq("Contacto1Nombre"), eq("68556790743"), eq("email1@gmail.com"), eq(obituary_test))).thenReturn(null); // Cambiado a when()

        // Verificar la interacción con los mocks
        verify(customerRepository).findById(1L);
        assertEquals(1L, customerRepository.findById(1L).get().getId());
        verify(imageTemplateService).findById(1L);
        verify(receiverService, times(1)).saveObituaryReceiver(eq("Contacto1Nombre"), eq("68556790743"), eq("email1@gmail.com"), eq(obituary_test));
    
        // Verificar que el obituario no sea null
        assertNotNull(obituary_test);
    
        // Verificar que los datos del obituario son correctos
        assertEquals("Alfonso Manuel Giraldillo", obituary_test.getName());
        assertEquals("Esto es un mensaje de despedida", obituary_test.getFarewellMessage());
        assertEquals("Esto es una frase de despedida", obituary_test.getFarewellPhrase());
        assertEquals("uploaded-image-url", obituary_test.getCustomImageUrl());  // Verificar la URL de la imagen subida
        assertNull(obituary_test.getBirthDate());  // Verificar que la fecha de nacimiento es null
        assertNull(obituary_test.getDeathDate());  // Verificar que la fecha de fallecimiento es null
    
        // Verificar que los contactos han sido correctamente guardados
        assertEquals(1, contacts.size());  // Sólo un contacto
        for (ObituraryRequestDto.ContactDto contact : contacts) {
            verify(receiverService).saveObituaryReceiver(eq(contact.getName()), eq(contact.getPhone()), eq(contact.getEmail()), eq(obituary_test));
        }
    }

    @Test
    void testGetObituaryById_WhenObituaryExists() {

        Long obituaryId = 1L;
        Obituary mockObituary = new Obituary();
        mockObituary.setId(obituaryId);
        mockObituary.setWordColor(null);

        when(obituaryRepository.findById(obituaryId)).thenReturn(Optional.of(mockObituary));


        Obituary result = obituaryService.getObituaryById(obituaryId);

        assertNotNull(result);
        assertEquals("0,0,0", result.getWordColor()); // Verifica que el color por defecto se asigna
        verify(obituaryRepository, times(1)).findById(obituaryId);
    }

    @Test
    void testGetObituaryById_WhenObituaryNotFound() {

        Long obituaryId = 1L;
        when(obituaryRepository.findById(obituaryId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            obituaryService.getObituaryById(obituaryId);
        });
        assertEquals("Obituary not found", exception.getMessage());
        verify(obituaryRepository, times(1)).findById(obituaryId);
    }

    

    // Otro caso de prueba, por ejemplo, cuando el customer no se encuentra
    @Test
    public void testCreateObituaryWithReceivers_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Simula que el customer no se encuentra
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            obituaryService.createObituaryWithReceivers(requestDto, 1L);
        });

        // Verificar el mensaje de error
        assertEquals("Customer not found", exception.getMessage());
    }


    // Caso: cuando no se encuentra el template de imagen
    @Test
    public void testCreateObituaryWithReceivers_ImageTemplateNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(imageTemplateService.findById(1L)).thenThrow(new RuntimeException("Image template not found"));

        // Ejecutar y verificar la excepción
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            obituaryService.createObituaryWithReceivers(requestDto, 1L);
        });

        assertEquals("Image template not found", exception.getMessage());
    }




}
