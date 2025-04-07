package com.caronte.caronte.obituary;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.deathCertificate.DeathCertificateService;
import com.caronte.caronte.deathCertificate.DTOs.DeathCertificateRequestDTO;
import com.caronte.caronte.imageTemplate.ImageTemplate;
import com.caronte.caronte.imageTemplate.ImageTemplateRepository;
import com.caronte.caronte.imageTemplate.ImageTemplateService;
import com.caronte.caronte.obituary.DTOs.ObituraryRequestDto;
import com.caronte.caronte.obituary.DTOs.ObituraryRequestDto.ContactDto;
import com.caronte.caronte.receiver.Receiver;
import com.caronte.caronte.receiver.ReceiverRepository;
import com.caronte.caronte.receiver.ReceiverService;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.util.exceptions.ResourceNotFound;

@SpringBootTest
public class ObituaryServiceTest {

    @MockitoBean
    private ObituaryRepository obituaryRepository;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private ImageTemplateRepository imageTemplateRepository;

    @MockitoBean
    private ReceiverRepository receiverRepository;

    @MockitoBean
    private ImageTemplateService imageTemplateService;

    @MockitoBean
    private MediaHandler mediaHandler;

    @MockitoBean
    private ReceiverService receiverService; // <--- Esto debe ser @MockBean también

    @MockitoBean
    private DeathCertificateService deathCertificateService;

    @Autowired
    private ObituaryService obituaryService;

    private ObituraryRequestDto requestDto;
    private Customer customer;
    private ImageTemplate imageTemplate;
    private Obituary obituary;
    private Obituary obituary2;
    private DeathCertificate certificate;
    private List<ContactDto> contacts;
    private ContactDto contact1;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("Alfonso Manuel Giraldillo");
        customer.setEmail("alfonso@example.com");
        customer.setDni("12345678A");

        contacts = new ArrayList<>();
        contact1 = new ContactDto();
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
        requestDto.setCustomImage(
                "https://static.nationalgeographicla.com/files/styles/image_3200/public/comedy-wildlife-awards-squirel-stop.jpg");

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
        // Arrange
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        ImageTemplate imageTemplate = new ImageTemplate();
        imageTemplate.setId(1L);

        ContactDto contact1 = new ContactDto();
        contact1.setName("Receiver Name");
        contact1.setEmail("receiver@example.com");
        contact1.setPhone("123456789");

        List<ContactDto> contacts = List.of(contact1);

        ObituraryRequestDto requestDto = new ObituraryRequestDto();
        requestDto.setName("Alfonso Manuel Giraldillo");
        requestDto.setFarewellMessage("Esto es un mensaje de despedida");
        requestDto.setFarewellPhrase("Esto es una frase de despedida");
        requestDto.setImageTemplate_id(1L);
        requestDto.setContacts(contacts);
        requestDto.setCustomImage("uploaded-image-url");
        requestDto.setIsMine(true);

        Obituary obituary = new Obituary();
        obituary.setName(requestDto.getName());
        obituary.setFarewellMessage(requestDto.getFarewellMessage());
        obituary.setFarewellPhrase(requestDto.getFarewellPhrase());
        obituary.setCustomImageUrl(requestDto.getCustomImage());
        obituary.setCustomer(customer);
        obituary.setImageTemplate(imageTemplate);
        List<Receiver> receivers = contacts.stream().map(contactDto -> Receiver.parse(contactDto, obituary)).toList();

        // Mocks
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(imageTemplateRepository.findById(1L)).thenReturn(Optional.of(imageTemplate));
        when(obituaryRepository.saveAndFlush(any(Obituary.class))).thenReturn(obituary);
        when(receiverRepository.saveAll(anyList())).thenReturn(receivers);
        // Act
        Obituary obituary_test = obituaryService.createObituaryWithReceivers(requestDto, customer.getId());

        // Assert
        verify(customerRepository).findById(1L);
        verify(imageTemplateRepository).findById(1L);
        verify(obituaryRepository, times(1)).saveAndFlush(any(Obituary.class));

        assertNotNull(obituary_test);
        assertEquals("Alfonso Manuel Giraldillo", obituary_test.getName());
        assertEquals("Esto es un mensaje de despedida", obituary_test.getFarewellMessage());
        assertEquals("Esto es una frase de despedida", obituary_test.getFarewellPhrase());
        assertEquals("uploaded-image-url", obituary_test.getCustomImageUrl());
        assertNull(obituary_test.getBirthDate());
        assertNull(obituary_test.getDeathDate());

        assertEquals(1, contacts.size());
    }

    @Test
    public void testCreateObituaryWithReceiversIsMineFalse() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(imageTemplateRepository.findById(1L)).thenReturn(Optional.of(imageTemplate));

        requestDto.setIsMine(false);
        requestDto.setDeathDate(LocalDate.of(2023, 3, 1));

        DeathCertificateRequestDTO deathCertificateRequestDTO = new DeathCertificateRequestDTO();
        deathCertificateRequestDTO.setDni("12345678Z");
        deathCertificateRequestDTO.setFile("https://example.com/certificate.pdf");
        deathCertificateRequestDTO.setIsVerificate(true);

        requestDto.setDeathCertificate(deathCertificateRequestDTO);

        when(deathCertificateService.createDeathCertificateAndRelations(any(DeathCertificateRequestDTO.class),
                anyLong())).thenReturn(certificate);
        when(obituaryRepository.saveAndFlush(any(Obituary.class))).thenReturn(obituary2);
        Obituary obituary_test = obituaryService.createObituaryWithReceivers(requestDto, customer.getId());

        verify(customerRepository).findById(1L);
        verify(imageTemplateRepository).findById(1L);
        verify(obituaryRepository, times(1)).saveAndFlush(any(Obituary.class));

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
        when(imageTemplateRepository.findById(1L)).thenReturn(Optional.of(imageTemplate));

        requestDto.setWordColor(null);
        requestDto.setContacts(contacts);

        when(obituaryRepository.saveAndFlush(any(Obituary.class))).thenReturn(obituary);

        Obituary obituary_test = obituaryService.createObituaryWithReceivers(requestDto, customer.getId());

        verify(customerRepository).findById(1L);
        verify(imageTemplateRepository).findById(1L);
        verify(obituaryRepository, times(1)).saveAndFlush(any(Obituary.class));

        assertNotNull(obituary_test);
        assertEquals("0,0,0", obituary_test.getWordColor());
    }

    @Test
    public void testCreateObituaryWithNoContacts() {

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(imageTemplateRepository.findById(1L)).thenReturn(Optional.of(imageTemplate));
        requestDto.setContacts(new ArrayList<>());

        when(obituaryRepository.saveAndFlush(any(Obituary.class))).thenReturn(obituary);

        Obituary obituary_test = obituaryService.createObituaryWithReceivers(requestDto, customer.getId());

        verify(customerRepository).findById(1L);
        verify(imageTemplateRepository).findById(1L);
        verify(obituaryRepository, times(1)).saveAndFlush(any(Obituary.class));

        assertNotNull(obituary_test);
        assertTrue(requestDto.getContacts().isEmpty());
    }

    @Test
    public void testCreateObituaryWithMultipleContacts() {
        ContactDto contact1 = new ContactDto();
        contact1.setName("Contacto1");
        contact1.setPhone("123456789");
        contact1.setEmail("contact1@example.com");

        ContactDto contact2 = new ContactDto();
        contact2.setName("Contacto2");
        contact2.setPhone("987654321");
        contact2.setEmail("contact2@example.com");
        
        List<ContactDto> contacts = List.of(contact1, contact2);

        // Suponiendo que tienes un objeto requestDto, obituary, customer, imageTemplate inicializados
        requestDto.setContacts(contacts);

        Receiver receiver1 = Receiver.parse(contact1, obituary);
        Receiver receiver2 = Receiver.parse(contact2, obituary);
        List<Receiver> receivers = List.of(receiver1, receiver2);

        when(obituaryRepository.saveAndFlush(any(Obituary.class))).thenReturn(obituary);
        when(customerRepository.findById(eq(1L))).thenReturn(Optional.of(customer));
        when(imageTemplateRepository.findById(eq(1L))).thenReturn(Optional.of(imageTemplate)); // corregido aquí
        when(receiverRepository.saveAll(anyList())).thenReturn(receivers);
        when(receiverService.saveObituaryReceiver(eq(contact2), eq(obituary))).thenReturn(receiver2);

        // Act
        Obituary result = obituaryService.createObituaryWithReceivers(requestDto, customer.getId());

        // Assert
        verify(customerRepository).findById(1L);
        verify(imageTemplateRepository).findById(1L);
        verify(obituaryRepository, times(1)).saveAndFlush(any(Obituary.class));
        assertNotNull(result);
    }

    @Test
    void testGetObituaryById_WhenObituaryExists() {

        Long obituaryId = 1L, customerId = 1L;
        Obituary mockObituary = new Obituary();
        mockObituary.setId(obituaryId);
        mockObituary.setWordColor(null);
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        mockObituary.setCustomer(mockCustomer);

        when(obituaryRepository.findById(obituaryId)).thenReturn(Optional.of(mockObituary));

        Obituary result = obituaryService.getObituaryById(obituaryId, customerId);

        assertNotNull(result);
        assertEquals("0,0,0", result.getWordColor());
        verify(obituaryRepository, times(1)).findById(obituaryId);
    }

    @Test
    void testGetObituaryById_WhenObituaryNotFound() {

        Long obituaryId = 1L;
        when(obituaryRepository.findById(obituaryId)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> {
            obituaryService.getObituaryById(obituaryId, anyLong());
        });
        assertEquals("Obituary not found", exception.getReason());
        verify(obituaryRepository, times(1)).findById(obituaryId);
    }

    @Test
    public void testCreateObituaryWithReceivers_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> {
            obituaryService.createObituaryWithReceivers(requestDto, 1L);
        });

        assertEquals("Customer not found", exception.getReason());
    }

    @Test
    public void testCreateObituaryWithReceivers_ImageTemplateNotFound() {
        // Preparar el mock
        when(customerRepository.findById(eq(1L))).thenReturn(Optional.of(customer));
        when(imageTemplateRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Ejecutar el test y capturar la excepción
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> {
            obituaryService.createObituaryWithReceivers(requestDto, 1L);
        });

        // Verificar que el mensaje de la excepción sea el esperado
        assertEquals("Image template not found", exception.getReason());
    }

    @Test
    public void testUpdateObituaryWithReceivers_Success() {

        obituary.setIsMine(true);
        when(obituaryRepository.findById(obituary.getId())).thenReturn(Optional.of(obituary));
        when(imageTemplateRepository.findById(requestDto.getImageTemplate_id())).thenReturn(Optional.of(imageTemplate));
        when(obituaryRepository.saveAndFlush(any(Obituary.class))).thenReturn(obituary);
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));

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

        Obituary updatedObituary = obituaryService.updateObituaryWithReceivers(customer.getId(), obituary.getId(),
                updateRequest);

        assertEquals("Nuevo Nombre", updatedObituary.getName());
        assertEquals(LocalDate.of(1980, 1, 1), updatedObituary.getBirthDate());
        assertEquals(LocalDate.of(2020, 12, 31), updatedObituary.getDeathDate());
        assertEquals("Nuevo mensaje de despedida", updatedObituary.getFarewellMessage());
        assertEquals("Nueva frase de despedida", updatedObituary.getFarewellPhrase());
        assertEquals("https://example.com/nueva-imagen.jpg", updatedObituary.getCustomImageUrl());
        assertEquals("255,255,255", updatedObituary.getWordColor());
        assertEquals(imageTemplate, updatedObituary.getImageTemplate());
    }

    @Test
    public void testUpdateObituaryWithReceivers_NotMineRequest() {
        when(obituaryRepository.findById(obituary.getId())).thenReturn(Optional.of(obituary));

        ObituraryRequestDto updateRequest = new ObituraryRequestDto();
        updateRequest.setIsMine(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> obituaryService.updateObituaryWithReceivers(customer.getId(), obituary.getId(), updateRequest));
        assertEquals("You can't upload the obituary since it isn't yours", exception.getReason());
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

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> obituaryService.updateObituaryWithReceivers(customer.getId(), obituary.getId(), updateRequest));
        assertEquals("You are not allowed to update this obituary", exception.getReason());
    }

    @Test
    public void testUpdateObituaryWithReceivers_ChangeIsMineProperty() {
        obituary.setIsMine(true);
        when(obituaryRepository.findById(obituary.getId())).thenReturn(Optional.of(obituary));

        ObituraryRequestDto updateRequest = new ObituraryRequestDto();
        updateRequest.setIsMine(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> obituaryService.updateObituaryWithReceivers(customer.getId(), obituary.getId(), updateRequest));
        assertEquals("You can't upload the obituary since it isn't yours", exception.getReason());
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

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> obituaryService.deleteObituaryByCustomer(customerId, obituaryId));
        assertEquals("You are not allowed to delete this obituary", exception.getReason());
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

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> obituaryService.findById(obituaryId));
        assertEquals("Obituary not found", exception.getReason());
    }

    @Test
    public void testGetAllObituariesByCustomer_Success() {
        Long customerId = customer.getId();
        List<Obituary> obituaryList = new ArrayList<>();
        obituaryList.add(obituary);
        when(obituaryRepository.findByCustomerId(customerId)).thenReturn(obituaryList);

        List<Obituary> result = obituaryService.getAllObituariesByCustomer(customerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(obituaryRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    public void testGetAllObituariesByCustomer_NoObituaries() {
        Long customerId = customer.getId();
        when(obituaryRepository.findByCustomerId(customerId)).thenReturn(new ArrayList<>());

        List<Obituary> result = obituaryService.getAllObituariesByCustomer(customerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetObituaryById_Success() {
        Long obituaryId = obituary.getId();
        when(obituaryRepository.findById(obituaryId)).thenReturn(Optional.of(obituary));

        Obituary result = obituaryService.getObituaryById(obituaryId, obituary.getCustomer().getId());

        assertNotNull(result);
        assertEquals(obituaryId, result.getId());
        verify(obituaryRepository, times(1)).findById(obituaryId);
    }

    @Test
    public void testGetObituaryById_NotFound() {
        Long obituaryId = 99L;
        when(obituaryRepository.findById(obituaryId)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> obituaryService.getObituaryById(obituaryId, anyLong()));
        assertEquals("Obituary not found", exception.getReason());
    }

    @Test
    public void testFindObituaryByCustomerDni_Success() {
        String dni = "12345678A";
        List<Obituary> obituaryList = new ArrayList<>();
        obituaryList.add(obituary);
        when(obituaryRepository.findByCustomerDni(dni)).thenReturn(obituaryList);

        List<Obituary> result = obituaryService.findObituaryByCustomerDni(dni);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(obituaryRepository, times(1)).findByCustomerDni(dni);
    }

    @Test
    public void testFindObituaryByCustomerDni_NoObituaries() {
        String dni = "12345678A";
        when(obituaryRepository.findByCustomerDni(dni)).thenReturn(new ArrayList<>());

        List<Obituary> result = obituaryService.findObituaryByCustomerDni(dni);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
