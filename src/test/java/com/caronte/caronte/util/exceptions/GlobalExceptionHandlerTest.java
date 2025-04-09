package com.caronte.caronte.util.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.util.ErrorHandler;

// Se asume que ErrorHandler y ErrorHandlerException están implementados en el paquete correspondiente.
// Por ejemplo, ErrorHandler podría tener un constructor que acepte un mensaje, y ErrorHandlerException
// un constructor que reciba un ErrorHandler.
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleErrorHandlerException() {
        // Se crea un ErrorHandler dummy (se asume que tiene constructor o setter para asignar valores)
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorHandlerException exception = new ErrorHandlerException(errorHandler);

        ResponseEntity<ErrorHandler> responseEntity = globalExceptionHandler.handleErrorHandlerException(exception);
        // Se verifica que la respuesta tenga código de estado 400 (Bad Request)
        assertEquals(400, responseEntity.getStatusCodeValue());
        // Se comprueba que el cuerpo de la respuesta sea el mismo ErrorHandler que se pasó en la excepción
        assertEquals(errorHandler, responseEntity.getBody());
    }

    @Test
    void testHandleBadCrendential() {
        BadCredentialsException exception = new BadCredentialsException("dummy");
        ResponseEntity<String> responseEntity = globalExceptionHandler.handleBadCrendential(exception);
        assertEquals(400, responseEntity.getStatusCodeValue());
        assertEquals("Credenciales incorrectas", responseEntity.getBody());
    }

    @Test
    void testHandleMethodArgumentNotValidException() throws Exception {
        // Se crea un BeanPropertyBindingResult para simular errores de validación
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "field1", "must not be blank"));
        
        // Se crea la excepción de validación (el primer parámetro se puede pasar como null para test)
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<Map<String, String>> responseEntity = globalExceptionHandler.handleMethodArgumentNotValidException(exception);
        
        assertEquals(400, responseEntity.getStatusCodeValue());
        Map<String, String> errors = responseEntity.getBody();
        assertNotNull(errors);
        // Se verifica que el error para "field1" sea el mensaje esperado
        assertEquals("must not be blank", errors.get("field1"));
    }

    @Test
    void testHandleException() {
        Exception exception = new Exception("Generic error");
        ResponseEntity<Map<String, String>> responseEntity = globalExceptionHandler.handleException(exception);
        assertEquals(400, responseEntity.getStatusCodeValue());
        Map<String, String> errorMap = responseEntity.getBody();
        assertNotNull(errorMap);
        assertEquals("Generic error", errorMap.get("error"));
    }

    @Test
    void testHandleResponseStatusException() {
        ResponseStatusException exception = new ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "Test reason");
        ResponseEntity<Map<String, String>> responseEntity = globalExceptionHandler.handleResponseStatusException(exception);
        assertEquals(400, responseEntity.getStatusCodeValue());
        Map<String, String> errorMap = responseEntity.getBody();
        assertNotNull(errorMap);
        assertEquals("Test reason", errorMap.get("error"));
    }
}
