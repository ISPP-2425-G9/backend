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

        // Inicializar objetos mockeados y datos de prueba
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
        // Cadena base64 de la URL proporcionada en el JSON (convertimos la URL a base64 para simular la carga real)
        String base64Image = "https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg"; // Aquí deberías incluir la cadena base64 completa de la imagen
    
        // Crear un mock de un customer
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Alfonso Manuel Giraldillo");
        customer.setEmail("alfonso@example.com");
    
        // Crear un mock de ImageTemplate
        ImageTemplate imageTemplate = new ImageTemplate();
        imageTemplate.setId(1L);
    
        // Simular el comportamiento de los mocks
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer)); // Mock de customer
        when(imageTemplateService.findById(1L)).thenReturn(imageTemplate); // Mock de imageTemplate
    
        // Crear el DTO de solicitud de obituario
        ObituraryRequestDto requestDto = new ObituraryRequestDto();
        requestDto.setName("Alfonso Manuel Giraldillo");
        requestDto.setFarewellMessage("Esto es un mensaje de despedida");
        requestDto.setFarewellPhrase("Esto es una frase de despedida");
        requestDto.setImageTemplate_id(1L);
        requestDto.setIsMine(true);
        requestDto.setCustomImage(base64Image);  // Usamos la cadena base64 de la imagen
        requestDto.setBirthDate(null);
        requestDto.setDeathDate(null);
    
        // Crear los contactos
        List<ContactDto> contacts = new ArrayList<>();
        ContactDto contact1 = new ContactDto();
        contact1.setName("Contacto1Nombre");
        contact1.setPhone("68556790743");
        contact1.setEmail("email1@gmail.com");
        contacts.add(contact1);
        requestDto.setContacts(contacts);
    
        // Simular el comportamiento del servicio receiver (en vez de doNothing() usamos when())
        when(receiverService.saveObituaryReceiver(eq("Contacto1Nombre"), eq("68556790743"), eq("email1@gmail.com"), any(Obituary.class))).thenReturn(null); // Cambiado a when()
    
        // Ejecutar el método a probar
        Obituary obituary = obituaryService.createObituaryWithReceivers(requestDto, customer.getId());
    
        // Verificar la interacción con los mocks
        verify(customerRepository).findById(1L);
        verify(imageTemplateService).findById(1L);
        verify(receiverService, times(1)).saveObituaryReceiver(eq("Contacto1Nombre"), eq("68556790743"), eq("email1@gmail.com"), eq(obituary));
    
        // Verificar que el obituario no sea null
        assertNotNull(obituary);
    
        // Verificar que los datos del obituario son correctos
        assertEquals("Alfonso Manuel Giraldillo", obituary.getName());
        assertEquals("Esto es un mensaje de despedida", obituary.getFarewellMessage());
        assertEquals("Esto es una frase de despedida", obituary.getFarewellPhrase());
        assertEquals("uploaded-image-url", obituary.getCustomImageUrl());  // Verificar la URL de la imagen subida
        assertNull(obituary.getBirthDate());  // Verificar que la fecha de nacimiento es null
        assertNull(obituary.getDeathDate());  // Verificar que la fecha de fallecimiento es null
    
        // Verificar que los contactos han sido correctamente guardados
        assertEquals(1, contacts.size());  // Sólo un contacto
        for (ObituraryRequestDto.ContactDto contact : contacts) {
            verify(receiverService).saveObituaryReceiver(eq(contact.getName()), eq(contact.getPhone()), eq(contact.getEmail()), eq(obituary));
        }
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

    // Caso: cuando la imagen base64 es inválida
    @Test
    public void testCreateObituaryWithReceivers_InvalidBase64Image() {
        String invalidBase64 = "data:image/jpeg;base64,invalidBase64";  // Cadena base64 inválida

        requestDto.setCustomImage(invalidBase64);  

        // Ejecutar y verificar que se lanza la excepción
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            obituaryService.createObituaryWithReceivers(requestDto, customer.getId());
        });

        assertEquals("Invalid base64 image", exception.getMessage());
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

    // Caso: cuando los contactos no son válidos (nombre vacío)
    @Test
    public void testCreateObituaryWithReceivers_InvalidContactData() {
        // Crear un contacto inválido (nombre vacío)
        List<ContactDto> contacts = new ArrayList<>();
        ContactDto contact1 = new ContactDto();
        contact1.setName("");
        contact1.setPhone("68556790743");
        contact1.setEmail("email1@gmail.com");
        contacts.add(contact1);
        requestDto.setContacts(contacts);

        // Ejecutar y verificar la excepción
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            obituaryService.createObituaryWithReceivers(requestDto, 1L);
        });

        assertEquals("El nombre del contacto es obligatorio.", exception.getMessage());
    }

    // Caso: cuando la URL de la imagen no es base64, es una URL normal
    @Test
public void testCreateObituaryWithReceivers_ValidImageUrl() {
    String imageUrl = "https://example.com/image.jpg";  // URL de imagen estándar

    requestDto.setCustomImage(imageUrl);  // Usar la URL de la imagen estándar

    // Ejecutar el método
    Obituary obituary = obituaryService.createObituaryWithReceivers(requestDto, 1L);

    // Verificar que la imagen se haya procesado correctamente
    assertEquals("uploaded-image-url", obituary.getCustomImageUrl());
}

    // Caso: cuando el wordColor es nulo, debe asignarse un valor por defecto
    @Test
    public void testCreateObituaryWithReceivers_DefaultWordColor() {
        requestDto.setWordColor(null);  // Asignar null al color

        // Ejecutar el método
        Obituary obituary = obituaryService.createObituaryWithReceivers(requestDto, 1L);

        // Verificar que el color de la palabra es asignado correctamente a "0,0,0" si es null
        assertEquals("0,0,0", obituary.getWordColor());
    }
}
