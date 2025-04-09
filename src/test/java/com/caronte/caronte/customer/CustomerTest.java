package com.caronte.caronte.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;
import org.junit.jupiter.api.Test;

public class CustomerTest {

    @Test
    void testHasDni_whenDniMatches_thenReturnTrue() {
        // Arrange
        Customer customer = new Customer();
        customer.setDni("123456789");

        // Act
        boolean result = customer.hasDni("123456789");

        // Assert
        assertTrue(result, "Se esperaba que el método retorne true cuando el DNI coincide");
    }

    @Test
    void testHasDni_whenDniDoesNotMatch_thenReturnFalse() {
        // Arrange
        Customer customer = new Customer();
        customer.setDni("123456789");

        // Act
        boolean result = customer.hasDni("987654321");

        // Assert
        assertFalse(result, "Se esperaba que el método retorne false cuando el DNI no coincide");
    }

    @Test
    void testUpdate() {
        // Arrange: se crea un cliente con valores iniciales (por herencia, se asume que se heredan email, name y telephone)
        Customer customer = new Customer();
        customer.setEmail("old@example.com");
        customer.setName("Old Name");
        customer.setTelephone("000000000");

        // Se crea un request con los datos nuevos para actualizar
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
        updateRequest.setEmail("new@example.com");
        updateRequest.setFullName("New Name");
        updateRequest.setTelephone("111111111");

        // Act: se invoca el método update que debe modificar el cliente
        customer.update(updateRequest);

        // Assert: se verifica que los datos se hayan actualizado correctamente
        assertEquals("new@example.com", customer.getEmail(), "El email debe haber sido actualizado");
        assertEquals("New Name", customer.getName(), "El nombre debe haber sido actualizado");
        assertEquals("111111111", customer.getTelephone(), "El teléfono debe haber sido actualizado");
    }
}
