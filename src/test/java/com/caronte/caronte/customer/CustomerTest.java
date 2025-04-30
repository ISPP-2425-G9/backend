package com.caronte.caronte.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;
import org.junit.jupiter.api.Test;

public class CustomerTest {

    @Test
    void testHasDni_whenDniMatches_thenReturnTrue() {
        Customer customer = new Customer();
        customer.setDni("123456789");
        boolean result = customer.hasDni("123456789");

        assertTrue(result, "Se esperaba que el método retorne true cuando el DNI coincide");
    }

    @Test
    void testHasDni_whenDniDoesNotMatch_thenReturnFalse() {
        Customer customer = new Customer();
        customer.setDni("123456789");

        boolean result = customer.hasDni("987654321");

        assertFalse(result, "Se esperaba que el método retorne false cuando el DNI no coincide");
    }

    @Test
    void testUpdate() {
        Customer customer = new Customer();
        customer.setEmail("old@example.com");
        customer.setName("Old Name");
        customer.setTelephone("000000000");

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
        updateRequest.setEmail("new@example.com");
        updateRequest.setFullName("New Name");
        updateRequest.setTelephone("111111111");

        customer.update(updateRequest);

        assertEquals("new@example.com", customer.getEmail(), "El email debe haber sido actualizado");
        assertEquals("New Name", customer.getName(), "El nombre debe haber sido actualizado");
        assertEquals("111111111", customer.getTelephone(), "El teléfono debe haber sido actualizado");
    }
}
