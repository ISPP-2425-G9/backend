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
import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.deathCertificate.DeathCertificateRequestDTO;
import com.caronte.caronte.deathCertificate.DeathCertificateService;

import java.time.LocalDate;
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
    private Obituary obituary;
    private Obituary obituary2;
    private DeathCertificate certificate;
    private List<ContactDto> contacts;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alfonso Manuel Giraldillo");
        customer.setEmail("alfonso@example.com");
        customer.setDni("12345678A");

        contacts = new ArrayList<>();
        ContactDto contact1 = new ContactDto();
        contact1.setName("Contacto1Nombre");
        contact1.setPhone("68556790743");
        contact1.setEmail("email1@gmail.com");
        contacts.add(contact1);

        imageTemplate = new ImageTemplate();
        imageTemplate.setId(1L);

        certificate = new DeathCertificate();
        certificate.setId(1L);
        certificate.setUrl("https://example.com/certificate.pdf");

        requestDto = new ObituraryRequestDto();
        requestDto.setName("Alfonso Manuel Giraldillo");
        requestDto.setFarewellMessage("Esto es un mensaje de despedida");
        requestDto.setFarewellPhrase("Esto es una frase de despedida");
        requestDto.setImageTemplate_id(1L);
        requestDto.setIsMine(true);
        requestDto.setBirthDate(null);
        requestDto.setDeathDate(null);
        requestDto.setContacts(contacts);
        requestDto.setCustomImage("https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg"); 
        
    
        obituary = new Obituary();
        obituary.setId(1L);
        obituary.setName("Alfonso Manuel Giraldillo");
        obituary.setFarewellMessage("Esto es un mensaje de despedida");
        obituary.setFarewellPhrase("Esto es una frase de despedida");
        obituary.setCustomImageUrl("uploaded-image-url");
        obituary.setBirthDate(null);
        obituary.setWordColor("0,0,0");
        obituary.setDeathDate(null);
        obituary.setCustomer(customer);
        obituary.setImageTemplate(imageTemplate);
        obituary.setDeathCertificate(null);
        obituary.setIsMine(true);
        

        obituary2 = new Obituary();
        obituary2.setId(2L);
        obituary2.setName("Alfonso Manuel Giraldillo");
        obituary2.setFarewellMessage("Esto es un mensaje de despedida");
        obituary2.setFarewellPhrase("Esto es una frase de despedida");
        obituary2.setCustomImageUrl("uploaded-image-url");
        obituary2.setBirthDate(null);
        obituary2.setDeathDate(LocalDate.of(2023, 3, 1));
        obituary2.setCustomer(customer);
        obituary2.setImageTemplate(imageTemplate);
        obituary2.setDeathCertificate(null);
        obituary2.setIsMine(false);



    }
@Test
    public void testCreateObituaryWithReceiversIsMineTrue() {
    
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(imageTemplateService.findById(1L)).thenReturn(imageTemplate); 
            
        when(obituaryRepository.save(any(Obituary.class))).thenReturn(obituary);
        Obituary obituary_test = obituaryService.createObituaryWithReceivers(requestDto, customer.getId());
        when(receiverService.saveObituaryReceiver(eq("Contacto1Nombre"), eq("68556790743"), eq("email1@gmail.com"), eq(obituary_test))).thenReturn(null);

        verify(customerRepository).findById(1L);
        verify(imageTemplateService).findById(1L);
        verify(receiverService, times(1)).saveObituaryReceiver(eq("Contacto1Nombre"), eq("68556790743"), eq("email1@gmail.com"), eq(obituary_test));
        verify(obituaryRepository, times(1)).save(any(Obituary.class));
        assertNotNull(obituary_test);
        assertEquals("Alfonso Manuel Giraldillo", obituary_test.getName());
        assertEquals("Esto es un mensaje de despedida", obituary_test.getFarewellMessage());
        assertEquals("Esto es una frase de despedida", obituary_test.getFarewellPhrase());
        assertEquals("uploaded-image-url", obituary_test.getCustomImageUrl());  
        assertNull(obituary_test.getBirthDate());  
        assertNull(obituary_test.getDeathDate());  
    
        assertEquals(1, contacts.size());  
        for (ObituraryRequestDto.ContactDto contact : contacts) {
            verify(receiverService).saveObituaryReceiver(eq(contact.getName()), eq(contact.getPhone()), eq(contact.getEmail()), eq(obituary_test));
        }
    }

    @Test
    public void testCreateObituaryWithReceiversIsMineFalse() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(imageTemplateService.findById(1L)).thenReturn(imageTemplate);
    
        requestDto.setIsMine(false);
        requestDto.setDeathDate(LocalDate.of(2023, 3, 1));

        DeathCertificateRequestDTO deathCertificateRequestDTO = new DeathCertificateRequestDTO();
        deathCertificateRequestDTO.setDni("12345678Z");
        deathCertificateRequestDTO.setFile("https://example.com/certificate.pdf");
        deathCertificateRequestDTO.setIsVerificate(true);

        requestDto.setDeathCertificate(deathCertificateRequestDTO);

        when(deathCertificateService.createDeathCertificateAndRelations(any(DeathCertificateRequestDTO.class), anyLong())).thenReturn(certificate);
        when(obituaryRepository.save(any(Obituary.class))).thenReturn(obituary2);
        Obituary obituary_test = obituaryService.createObituaryWithReceivers(requestDto, customer.getId());
        
        verify(customerRepository).findById(1L);
        verify(imageTemplateService).findById(1L);
        verify(obituaryRepository, times(1)).save(any(Obituary.class));
        
        assertNotNull(obituary_test);
        assertEquals("Alfonso Manuel Giraldillo", obituary_test.getName());
        assertEquals("Esto es un mensaje de despedida", obituary_test.getFarewellMessage());
        assertEquals("Esto es una frase de despedida", obituary_test.getFarewellPhrase());
        assertEquals("uploaded-image-url", obituary_test.getCustomImageUrl());
        assertNull(obituary_test.getBirthDate());
        assertNotNull(obituary_test.getDeathDate());
    }

@Test
public void testCreateObituaryWithDefaultWordColor() {
    
    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(imageTemplateService.findById(1L)).thenReturn(imageTemplate);
    
    requestDto.setWordColor(null);  
    requestDto.setContacts(contacts);
    
    when(obituaryRepository.save(any(Obituary.class))).thenReturn(obituary);
    
    Obituary obituary_test = obituaryService.createObituaryWithReceivers(requestDto, customer.getId());
    
    verify(customerRepository).findById(1L);
    verify(imageTemplateService).findById(1L);
    verify(obituaryRepository, times(1)).save(any(Obituary.class));
    

    assertNotNull(obituary_test);
    assertEquals("0,0,0", obituary_test.getWordColor());
}

@Test
public void testCreateObituaryWithNoContacts() {
    
    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(imageTemplateService.findById(1L)).thenReturn(imageTemplate);
    requestDto.setContacts(new ArrayList<>());  
    
    when(obituaryRepository.save(any(Obituary.class))).thenReturn(obituary);
    
    Obituary obituary_test = obituaryService.createObituaryWithReceivers(requestDto, customer.getId());

    verify(customerRepository).findById(1L);
    verify(imageTemplateService).findById(1L);
    verify(obituaryRepository, times(1)).save(any(Obituary.class));
    
    assertNotNull(obituary_test);
    assertTrue(requestDto.getContacts().isEmpty());  
}

@Test
public void testCreateObituaryWithMultipleContacts() {

    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(imageTemplateService.findById(1L)).thenReturn(imageTemplate);

    List<ObituraryRequestDto.ContactDto> contacts = new ArrayList<>();
    ObituraryRequestDto.ContactDto contact1 = new ObituraryRequestDto.ContactDto();
    contact1.setName("Contacto1");
    contact1.setPhone("123456789");
    contact1.setEmail("contact1@example.com");
    contacts.add(contact1);
    
    ObituraryRequestDto.ContactDto contact2 = new ObituraryRequestDto.ContactDto();
    contact2.setName("Contacto2");
    contact2.setPhone("987654321");
    contact2.setEmail("contact2@example.com");
    contacts.add(contact2);
    
    requestDto.setContacts(contacts); 
    
    when(obituaryRepository.save(any(Obituary.class))).thenReturn(obituary);
    when(receiverService.saveObituaryReceiver(anyString(), anyString(), anyString(), any(Obituary.class))).thenReturn(null);
    
    Obituary obituary_test = obituaryService.createObituaryWithReceivers(requestDto, customer.getId());
    
    verify(customerRepository).findById(1L);
    verify(imageTemplateService).findById(1L);
    verify(obituaryRepository, times(1)).save(any(Obituary.class));
    verify(receiverService, times(2)).saveObituaryReceiver(anyString(), anyString(), anyString(), any(Obituary.class)); 
    assertNotNull(obituary_test);
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
        assertEquals("0,0,0", result.getWordColor());
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

    
    @Test
    public void testCreateObituaryWithReceivers_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            obituaryService.createObituaryWithReceivers(requestDto, 1L);
        });

        assertEquals("Customer not found", exception.getMessage());
    }


    @Test
    public void testCreateObituaryWithReceivers_ImageTemplateNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(imageTemplateService.findById(1L)).thenThrow(new RuntimeException("Image template not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            obituaryService.createObituaryWithReceivers(requestDto, 1L);
        });

        assertEquals("Image template not found", exception.getMessage());
    }

    @Test
public void testUpdateObituaryWithReceivers_Success() {
    
    obituary.setIsMine(true);
    when(obituaryRepository.findById(obituary.getId())).thenReturn(Optional.of(obituary));
    when(imageTemplateService.findById(requestDto.getImageTemplate_id())).thenReturn(imageTemplate);
    when(obituaryRepository.save(any(Obituary.class))).thenAnswer(i -> i.getArguments()[0]);

    ObituraryRequestDto updateRequest = new ObituraryRequestDto();
    updateRequest.setName("Nuevo Nombre");
    updateRequest.setFarewellMessage("Nuevo mensaje de despedida");
    updateRequest.setFarewellPhrase("Nueva frase de despedida");
    updateRequest.setBirthDate(LocalDate.of(1980, 1, 1));
    updateRequest.setDeathDate(LocalDate.of(2020, 12, 31));
    updateRequest.setIsMine(true);
    updateRequest.setImageTemplate_id(imageTemplate.getId());
    updateRequest.setCustomImage("https://example.com/nueva-imagen.jpg");
    updateRequest.setWordColor("255,255,255");

    List<ObituraryRequestDto.ContactDto> contacts = new ArrayList<>();
    ObituraryRequestDto.ContactDto contact = new ObituraryRequestDto.ContactDto();
    contact.setName("ContactoActualizado");
    contact.setPhone("1234567890");
    contact.setEmail("actualizado@example.com");
    contacts.add(contact);
    updateRequest.setContacts(contacts);

    Obituary updatedObituary = obituaryService.updateObituaryWithReceivers(customer.getId(), obituary.getId(), updateRequest);

    assertEquals("Nuevo Nombre", updatedObituary.getName());
    assertEquals(LocalDate.of(1980, 1, 1), updatedObituary.getBirthDate());
    assertEquals(LocalDate.of(2020, 12, 31), updatedObituary.getDeathDate());
    assertEquals("Nuevo mensaje de despedida", updatedObituary.getFarewellMessage());
    assertEquals("Nueva frase de despedida", updatedObituary.getFarewellPhrase());
    assertEquals("https://example.com/nueva-imagen.jpg", updatedObituary.getCustomImageUrl());
    assertEquals("255,255,255", updatedObituary.getWordColor());
    assertEquals(imageTemplate, updatedObituary.getImageTemplate());
    verify(receiverService).deleteReceiversByObituaryId(updatedObituary);
    verify(receiverService).saveObituaryReceiver("ContactoActualizado", "1234567890", "actualizado@example.com", updatedObituary);
}

@Test
public void testUpdateObituaryWithReceivers_NotMineRequest() {
    when(obituaryRepository.findById(obituary.getId())).thenReturn(Optional.of(obituary));

    ObituraryRequestDto updateRequest = new ObituraryRequestDto();
    updateRequest.setIsMine(false);

    Exception exception = assertThrows(IllegalArgumentException.class, () ->
            obituaryService.updateObituaryWithReceivers(customer.getId(), obituary.getId(), updateRequest)
    );
    assertEquals("You can't upload the obituary since it isn't yours", exception.getMessage());
}

@Test
public void testUpdateObituaryWithReceivers_CustomerNotMatch() {
    Customer otroCustomer = new Customer();
    otroCustomer.setId(99L);
    obituary.setCustomer(otroCustomer);
    obituary.setIsMine(true);
    when(obituaryRepository.findById(obituary.getId())).thenReturn(Optional.of(obituary));

    ObituraryRequestDto updateRequest = new ObituraryRequestDto();
    updateRequest.setIsMine(true);

    Exception exception = assertThrows(IllegalArgumentException.class, () ->
            obituaryService.updateObituaryWithReceivers(customer.getId(), obituary.getId(), updateRequest)
    );
    assertEquals("You are not allowed to update this obituary", exception.getMessage());
}

@Test
public void testUpdateObituaryWithReceivers_ChangeIsMineProperty() {
    obituary.setIsMine(true);
    when(obituaryRepository.findById(obituary.getId())).thenReturn(Optional.of(obituary));

    ObituraryRequestDto updateRequest = new ObituraryRequestDto();
    updateRequest.setIsMine(false);

    Exception exception = assertThrows(IllegalArgumentException.class, () ->
            obituaryService.updateObituaryWithReceivers(customer.getId(), obituary.getId(), updateRequest)
    );
    assertEquals("You can't upload the obituary since it isn't yours", exception.getMessage());
}

@Test
public void testDeleteObituaryByCustomer_Success() {
    Long obituaryId = obituary.getId();
    Long customerId = customer.getId();

    when(obituaryRepository.findById(obituaryId)).thenReturn(Optional.of(obituary));

    obituaryService.deleteObituaryByCustomer(customerId, obituaryId);

    verify(obituaryRepository, times(1)).deleteById(obituaryId);
}

@Test
public void testDeleteObituaryByCustomer_CustomerNotMatch() {
    Long obituaryId = obituary.getId();
    Long customerId = 99L;

    when(obituaryRepository.findById(obituaryId)).thenReturn(Optional.of(obituary));

    Exception exception = assertThrows(IllegalArgumentException.class, () ->
            obituaryService.deleteObituaryByCustomer(customerId, obituaryId)
    );
    assertEquals("You are not allowed to delete this obituary", exception.getMessage());
}

@Test
public void testFindById_Success() {
    Long obituaryId = obituary.getId();
    when(obituaryRepository.findById(obituaryId)).thenReturn(Optional.of(obituary));

    Obituary foundObituary = obituaryService.findById(obituaryId);

    assertNotNull(foundObituary);
    assertEquals(obituaryId, foundObituary.getId());
    verify(obituaryRepository, times(1)).findById(obituaryId);
}

@Test
public void testFindById_NotFound() {
    Long obituaryId = 99L;
    when(obituaryRepository.findById(obituaryId)).thenReturn(Optional.empty());

    Exception exception = assertThrows(RuntimeException.class, () ->
            obituaryService.findById(obituaryId)
    );
    assertEquals("Obituary not found", exception.getMessage());
}

@Test
public void testGetAllObituariesByCustomer_Success() {
    Long customerId = customer.getId();
    List<Obituary> obituaryList = new ArrayList<>();
    obituaryList.add(obituary);
    when(obituaryRepository.findByCustomerId(customerId)).thenReturn(obituaryList);

    Iterable<Obituary> result = obituaryService.getAllObituariesByCustomer(customerId);

    assertNotNull(result);
    assertEquals(1, ((List<?>) result).size());
    verify(obituaryRepository, times(1)).findByCustomerId(customerId);
}

@Test
public void testGetAllObituariesByCustomer_NoObituaries() {
    Long customerId = customer.getId();
    when(obituaryRepository.findByCustomerId(customerId)).thenReturn(new ArrayList<>());

    Iterable<Obituary> result = obituaryService.getAllObituariesByCustomer(customerId);

    assertNotNull(result);
    assertEquals(0, ((List<?>) result).size());
}

@Test
public void testGetObituaryById_Success() {
    Long obituaryId = obituary.getId();
    when(obituaryRepository.findById(obituaryId)).thenReturn(Optional.of(obituary));

    Obituary result = obituaryService.getObituaryById(obituaryId);

    assertNotNull(result);
    assertEquals(obituaryId, result.getId());
    verify(obituaryRepository, times(1)).findById(obituaryId);
}

@Test
public void testGetObituaryById_NotFound() {
    Long obituaryId = 99L;
    when(obituaryRepository.findById(obituaryId)).thenReturn(Optional.empty());

    Exception exception = assertThrows(IllegalArgumentException.class, () ->
            obituaryService.getObituaryById(obituaryId)
    );
    assertEquals("Obituary not found", exception.getMessage());
}
 
@Test
public void testFindObituaryByCustomerDni_Success() {
    String dni = "12345678A";
    List<Obituary> obituaryList = new ArrayList<>();
    obituaryList.add(obituary);
    when(obituaryRepository.findByCustomerDni(dni)).thenReturn(obituaryList);

    Iterable<Obituary> result = obituaryService.findObituaryByCustomerDni(dni);

    assertNotNull(result);
    assertEquals(1, ((List<?>) result).size());
    verify(obituaryRepository, times(1)).findByCustomerDni(dni);
}

@Test
public void testFindObituaryByCustomerDni_NoObituaries() {
    String dni = "12345678A";
    when(obituaryRepository.findByCustomerDni(dni)).thenReturn(new ArrayList<>());

    Iterable<Obituary> result = obituaryService.findObituaryByCustomerDni(dni);

    assertNotNull(result);
    assertEquals(0, ((List<?>) result).size());
}

}
 