package com.caronte.caronte.emergencyContact;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.emergencyContact.DTOs.EmergencyContactDTO;

@ExtendWith(MockitoExtension.class)
public class EmergencyContactServiceTest {

    @Mock
    private EmergencyContactRepository emergencyContactRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private EmergencyContactService emergencyContactService;

    
    private Customer dummyCustomer;
    private EmergencyContact dummyContact;

    @BeforeEach
    void setUp() {
       
        dummyCustomer = new Customer();
        dummyCustomer.setId(1L);
        dummyCustomer.setEmail("customer@example.com");

       
        dummyContact = new EmergencyContact();
        dummyContact.setId(100L);
        dummyContact.setName("John Doe");
        dummyContact.setTelephone("123456789");
        dummyContact.setEmail("john@example.com");
        dummyContact.setCustomer(dummyCustomer);
    }

    @Test
    void testFindAll() {
        EmergencyContact contact1 = new EmergencyContact();
        contact1.setId(10L);
        contact1.setName("Alice");
        contact1.setTelephone("111111111");
        contact1.setEmail("alice@example.com");

        EmergencyContact contact2 = new EmergencyContact();
        contact2.setId(11L);
        contact2.setName("Bob");
        contact2.setTelephone("222222222");
        contact2.setEmail("bob@example.com");

        List<EmergencyContact> contacts = Arrays.asList(contact1, contact2);
        when(emergencyContactRepository.findAllByCustomerEmail("customer@example.com"))
                .thenReturn(contacts);

        List<EmergencyContactDTO> dtos = emergencyContactService.findAll("customer@example.com");
        assertNotNull(dtos);
        assertEquals(2, dtos.size());

        assertEquals("Alice", dtos.get(0).name());
        assertEquals("Bob", dtos.get(1).name());
    }

    @Test
    void testSave_success() {
        EmergencyContactDTO dto = new EmergencyContactDTO(null, "John Doe", "123456789", "john@example.com");

        when(emergencyContactRepository.existsByTelephoneAndCustomer("123456789", "customer@example.com")).thenReturn(false);
        when(emergencyContactRepository.existsByEmailAndCustomer("john@example.com", "customer@example.com")).thenReturn(false);
        
        when(customerRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(dummyCustomer));
      
        when(emergencyContactRepository.save(any(EmergencyContact.class))).thenAnswer(invocation -> {
            EmergencyContact ec = invocation.getArgument(0);
            ec.setId(200L);
            return ec;
        });

        EmergencyContactDTO result = emergencyContactService.save(dto, "customer@example.com");
        assertNotNull(result);
        assertEquals(200L, result.id());
        assertEquals("John Doe", result.name());
    }
    
    @SuppressWarnings("null")
    @Test
    void testSave_duplicateTelephone_shouldThrow() {
        EmergencyContactDTO dto = new EmergencyContactDTO(null, "John Doe", "123456789", "john@example.com");
        when(emergencyContactRepository.existsByTelephoneAndCustomer("123456789", "customer@example.com")).thenReturn(true);
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, 
            () -> emergencyContactService.save(dto, "customer@example.com"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("El número de teléfono ya está registrado"));
    }
    
    @SuppressWarnings("null")
    @Test
    void testSave_duplicateEmail_shouldThrow() {
        EmergencyContactDTO dto = new EmergencyContactDTO(null, "John Doe", "123456789", "john@example.com");
        when(emergencyContactRepository.existsByTelephoneAndCustomer("123456789", "customer@example.com")).thenReturn(false);
        when(emergencyContactRepository.existsByEmailAndCustomer("john@example.com", "customer@example.com")).thenReturn(true);
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, 
            () -> emergencyContactService.save(dto, "customer@example.com"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("El email ya está registrado"));
    }
    
    @Test
    void testUpdate_success() {
       
        EmergencyContact existing = new EmergencyContact();
        existing.setId(300L);
        existing.setName("Old Name");
        existing.setTelephone("111111111");
        existing.setEmail("old@example.com");
        existing.setCustomer(dummyCustomer);
        
        when(emergencyContactRepository.findById(300L)).thenReturn(Optional.of(existing));
        when(emergencyContactRepository.save(any(EmergencyContact.class))).thenAnswer(invocation -> invocation.getArgument(0));
       
        EmergencyContactDTO dto = new EmergencyContactDTO(300L, "New Name", "222222222", "new@example.com");
        when(emergencyContactRepository.existsByTelephoneAndCustomer("222222222", "customer@example.com")).thenReturn(false);
        when(emergencyContactRepository.existsByEmailAndCustomer("new@example.com", "customer@example.com")).thenReturn(false);
        
        EmergencyContactDTO updatedDTO = emergencyContactService.update(dto, 300L, "customer@example.com");
        assertNotNull(updatedDTO);
        assertEquals("New Name", updatedDTO.name());
    }
    
    @Test
    void testUpdate_notOwner_shouldThrow() {
        EmergencyContact existing = new EmergencyContact();
        existing.setId(400L);
        existing.setName("Old Name");
        existing.setTelephone("111111111");
        existing.setEmail("old@example.com");
        
        Customer otherCustomer = new Customer();
        otherCustomer.setEmail("other@example.com");
        existing.setCustomer(otherCustomer);
        
        when(emergencyContactRepository.findById(400L)).thenReturn(Optional.of(existing));
        
        EmergencyContactDTO dto = new EmergencyContactDTO(400L, "New Name", "222222222", "new@example.com");
        
        assertThrows(ResponseStatusException.class, () -> 
            emergencyContactService.update(dto, 400L, "customer@example.com"));
    }
    
    @SuppressWarnings("null")
    @Test
    void testUpdate_duplicateTelephone_shouldThrow() {
        EmergencyContact existing = new EmergencyContact();
        existing.setId(500L);
        existing.setName("Old Name");
        existing.setTelephone("111111111");
        existing.setEmail("old@example.com");
        existing.setCustomer(dummyCustomer);
        
        when(emergencyContactRepository.findById(500L)).thenReturn(Optional.of(existing));
        EmergencyContactDTO dto = new EmergencyContactDTO(500L, "New Name", "222222222", "old@example.com");
        when(emergencyContactRepository.existsByTelephoneAndCustomer("222222222", "customer@example.com")).thenReturn(true);
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> 
            emergencyContactService.update(dto, 500L, "customer@example.com"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("El número de teléfono ya está registrado."));
    }

    @SuppressWarnings("null")
    @Test
    void testUpdate_duplicateEmail_shouldThrow() {
        EmergencyContact existing = new EmergencyContact();
        existing.setId(600L);
        existing.setName("Old Name");
        existing.setTelephone("111111111");
        existing.setEmail("old@example.com");
        existing.setCustomer(dummyCustomer);
        
        when(emergencyContactRepository.findById(600L)).thenReturn(Optional.of(existing));
        EmergencyContactDTO dto = new EmergencyContactDTO(600L, "New Name", "111111111", "new@example.com");
        when(emergencyContactRepository.existsByEmailAndCustomer("new@example.com", "customer@example.com")).thenReturn(true);
        
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
            emergencyContactService.update(dto, 600L, "customer@example.com"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("El email ya está registrado."));
    }
    
    @Test
    void testDelete_success() {
        EmergencyContact existing = new EmergencyContact();
        existing.setId(700L);
        existing.setEmail("john@example.com");
        Customer cust = new Customer();
        cust.setEmail("customer@example.com");
        existing.setCustomer(cust);
        
        when(emergencyContactRepository.findById(700L)).thenReturn(Optional.of(existing));

        assertDoesNotThrow(() -> emergencyContactService.delete(700L, "customer@example.com"));
        verify(emergencyContactRepository).deleteById(700L);
    }
    
   
    @Test
    void testDelete_notOwner_shouldThrow() {
        EmergencyContact existing = new EmergencyContact();
        existing.setId(800L);
        existing.setEmail("john@example.com");
        Customer cust = new Customer();
        cust.setEmail("other@example.com");
        existing.setCustomer(cust);
        
        when(emergencyContactRepository.findById(800L)).thenReturn(Optional.of(existing));
        assertThrows(ResponseStatusException.class, () -> emergencyContactService.delete(800L, "customer@example.com"));
        verify(emergencyContactRepository, never()).deleteById(anyLong());
    }
}
