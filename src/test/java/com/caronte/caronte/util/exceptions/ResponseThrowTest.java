package com.caronte.caronte.util.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResponseThrowTest {

    // Test para el método check: no lanza excepción cuando la condición es true
    @Test
    void testCheck_NoExceptionWhenConditionTrue() {
        Assertions.assertDoesNotThrow(() ->
            ResponseThrow.check(true, HttpStatus.BAD_REQUEST, "Motivo de prueba")
        );
    }

    // Test para el método check: se lanza excepción cuando la condición es false
    @Test
    void testCheck_ExceptionWhenConditionFalse() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
            ResponseThrow.check(false, HttpStatus.BAD_REQUEST, "Motivo de prueba")
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        Assertions.assertEquals("Motivo de prueba", exception.getReason());
    }

    // Test para checkOrNotFound: no lanza excepción cuando la condición es true
    @Test
    void testCheckOrNotFound_NoException() {
        Assertions.assertDoesNotThrow(() ->
            ResponseThrow.checkOrNotFound(true, "No se encontró")
        );
    }

    // Test para checkOrNotFound: se lanza excepción con status NOT_FOUND cuando la condición es false
    @Test
    void testCheckOrNotFound_Exception() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
            ResponseThrow.checkOrNotFound(false, "Elemento no encontrado")
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        Assertions.assertEquals("Elemento no encontrado", exception.getReason());
    }

    // Test para checkOrBadRequest: no lanza excepción cuando la condición es true
    @Test
    void testCheckOrBadRequest_NoException() {
        Assertions.assertDoesNotThrow(() ->
            ResponseThrow.checkOrBadRequest(true, "Solicitud incorrecta")
        );
    }

    // Test para checkOrBadRequest: se lanza excepción con status BAD_REQUEST cuando la condición es false
    @Test
    void testCheckOrBadRequest_Exception() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
            ResponseThrow.checkOrBadRequest(false, "Solicitud incorrecta")
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        Assertions.assertEquals("Solicitud incorrecta", exception.getReason());
    }

    // Test para checkOrForbidden con mensaje personalizado: no lanza excepción cuando la condición es true
    @Test
    void testCheckOrForbiddenCustom_NoException() {
        Assertions.assertDoesNotThrow(() ->
            ResponseThrow.checkOrForbidden(true, "Acceso prohibido")
        );
    }

    // Test para checkOrForbidden con mensaje personalizado: se lanza excepción cuando la condición es false
    @Test
    void testCheckOrForbiddenCustom_Exception() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
            ResponseThrow.checkOrForbidden(false, "Acceso prohibido")
        );
        Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        Assertions.assertEquals("Acceso prohibido", exception.getReason());
    }

    // Test para el método checkOrForbidden con mensaje por defecto: no lanza excepción cuando la condición es true
    @Test
    void testCheckOrForbiddenDefault_NoException() {
        Assertions.assertDoesNotThrow(() ->
            ResponseThrow.checkOrForbidden(true)
        );
    }

    // Test para el método checkOrForbidden con mensaje por defecto: se lanza excepción con el mensaje predeterminado cuando la condición es false
    @Test
    void testCheckOrForbiddenDefault_Exception() {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () ->
            ResponseThrow.checkOrForbidden(false)
        );
        Assertions.assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        Assertions.assertEquals("You can't access this data", exception.getReason());
    }
}
