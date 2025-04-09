package com.caronte.caronte.emergencyContact;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.emergencyContact.DTOs.EmergencyContactDTO;

public class EmergencyContactTest {

    private EmergencyContact emergencyContact;
    private Customer customer;
    private EmergencyContactDTO initialDto;
    private EmergencyContactDTO updateDto;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("customer@example.com");
        
        initialDto = new EmergencyContactDTO(null, "John Doe", "123456789", "john@example.com");
        
        emergencyContact = new EmergencyContact(initialDto, customer);
    }

    @Test
    void testConstructor() {

        assertEquals("John Doe", emergencyContact.getName(), "El nombre debe coincidir con el DTO");
        assertEquals("123456789", emergencyContact.getTelephone(), "El teléfono debe coincidir con el DTO");
        assertEquals("john@example.com", emergencyContact.getEmail(), "El email debe coincidir con el DTO");
        assertEquals(customer, emergencyContact.getCustomer(), "El Customer asignado debe coincidir");
    }

    @Test
    void testUpdate() {
        updateDto = new EmergencyContactDTO(null, "Jane Smith", "987654321", "jane@example.com");
        emergencyContact.update(updateDto);

        assertEquals("Jane Smith", emergencyContact.getName(), "El nombre debe actualizarse");
        assertEquals("987654321", emergencyContact.getTelephone(), "El teléfono debe actualizarse");
        assertEquals("jane@example.com", emergencyContact.getEmail(), "El email debe actualizarse");
    }

    @Test
    void testHasCustomerEmail() {
        assertTrue(emergencyContact.hasCustomerEmail("customer@example.com"),
                "Debe retornar true para el email correcto");
        assertFalse(emergencyContact.hasCustomerEmail("wrong@example.com"),
                "Debe retornar false para un email distinto");
    }

    @Test
    void testHasTelephone() {
        assertTrue(emergencyContact.hasTelephone("123456789"),
                "Debe retornar true para el teléfono correcto");
        assertFalse(emergencyContact.hasTelephone("000000000"),
                "Debe retornar false para un teléfono incorrecto");
    }

    @Test
    void testHasEmail() {
        assertTrue(emergencyContact.hasEmail("john@example.com"),
                "Debe retornar true para el email correcto");
        assertFalse(emergencyContact.hasEmail("other@example.com"),
                "Debe retornar false para un email incorrecto");
    }
}
