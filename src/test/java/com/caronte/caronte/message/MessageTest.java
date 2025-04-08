package com.caronte.caronte.message;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.message.DTOs.MessageRequestDto;

class MessageTest {

    @Test
    void testGenerateUniqueRandomCode() throws Exception {
        // Usar reflexión para acceder al método privado estático generateUniqueRandomCode
        Method generateCodeMethod = Message.class.getDeclaredMethod("generateUniqueRandomCode");
        generateCodeMethod.setAccessible(true);
        String code = (String) generateCodeMethod.invoke(null);
        
        assertNotNull(code, "El código no debe ser null");
        assertEquals(5, code.length(), "El código debe tener 5 dígitos");
        assertTrue(code.matches("\\d{5}"), "El código debe contener solo dígitos");
    }

    @Test
    void testHasCustomerWithId() {
        Customer customer = new Customer();
        customer.setId(42L);

        Message message = new Message();
        message.setCustomer(customer);
        
        assertTrue(message.hasCustomerWithId(42L), "Debe retornar true cuando el ID coincide");
        assertFalse(message.hasCustomerWithId(43L), "Debe retornar false cuando el ID no coincide");
    }

    @Test
    void testConstructorWithRequestDto() {
        // Preparar el DTO de mensaje
        MessageRequestDto dto = new MessageRequestDto();
        dto.setTitle("Hello");
        dto.setBody("Test body");
        dto.setIsLastWill(true);

        // Preparar un Customer
        Customer customer = new Customer();
        customer.setId(1L);

        // Invocar el constructor sobrecargado que recibe un DTO y un Customer
        Message message = new Message(dto, customer);

        // Validaciones:
        assertEquals("Hello", message.getTitle(), "El título debe coincidir con el del DTO");
        assertEquals("Test body", message.getBody(), "El cuerpo debe coincidir con el del DTO");
        assertTrue(message.getIsLastWill(), "El isLastWill debe ser true");
        assertNotNull(message.getCode(), "El código generado no debe ser null");
        assertEquals(5, message.getCode().length(), "El código debe tener 5 caracteres");
        assertTrue(message.getCode().matches("\\d{5}"), "El código debe contener solo dígitos");
        assertEquals(customer, message.getCustomer(), "El Customer asignado debe coincidir");
    }
}
