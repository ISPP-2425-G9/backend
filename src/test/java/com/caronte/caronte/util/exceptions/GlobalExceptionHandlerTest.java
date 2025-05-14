package com.caronte.caronte.util.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.util.ErrorHandler;


public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleErrorHandlerException() {
        
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorHandlerException exception = new ErrorHandlerException(errorHandler);

        ResponseEntity<ErrorHandler> responseEntity = globalExceptionHandler.handleErrorHandlerException(exception);
        
        assertEquals(400, responseEntity.getStatusCode().value());
    
        assertEquals(errorHandler, responseEntity.getBody());
    }

    @Test
    void testHandleBadCrendential() {
        BadCredentialsException exception = new BadCredentialsException("dummy");
        ResponseEntity<String> responseEntity = globalExceptionHandler.handleBadCrendential(exception);
        assertEquals(400, responseEntity.getStatusCode().value());
        assertEquals("Credenciales incorrectas", responseEntity.getBody());
    }

    @Test
    void testHandleMethodArgumentNotValidException() throws Exception {

        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "field1", "must not be blank"));
        
        MethodParameter mockParameter = Mockito.mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(mockParameter, bindingResult);
        ResponseEntity<Map<String, String>> responseEntity = globalExceptionHandler.handleMethodArgumentNotValidException(exception);
        
        assertEquals(400, responseEntity.getStatusCode().value());
        Map<String, String> errors = responseEntity.getBody();
        assertNotNull(errors);

        assertEquals("must not be blank", errors.get("field1"));
    }

    @Test
    void testHandleException() {
        Exception exception = new Exception("Generic error");
        ResponseEntity<Map<String, String>> responseEntity = globalExceptionHandler.handleException(exception);
        assertEquals(400, responseEntity.getStatusCode().value());
        Map<String, String> errorMap = responseEntity.getBody();
        assertNotNull(errorMap);
        assertEquals("Generic error", errorMap.get("error"));
    }

    @Test
    void testHandleResponseStatusException() {
        ResponseStatusException exception = new ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "Test reason");
        ResponseEntity<Map<String, String>> responseEntity = globalExceptionHandler.handleResponseStatusException(exception);
        assertEquals(400, responseEntity.getStatusCode().value());
        Map<String, String> errorMap = responseEntity.getBody();
        assertNotNull(errorMap);
        assertEquals("Test reason", errorMap.get("error"));
    }
}
