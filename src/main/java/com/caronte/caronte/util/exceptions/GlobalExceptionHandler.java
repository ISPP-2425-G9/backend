package com.caronte.caronte.util.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.caronte.caronte.util.ErrorHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class, DataIntegrityViolationException.class})
    public ResponseEntity<String> handleResourceException(Exception ex) {
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler(ErrorHandlerException.class)
    public ResponseEntity<ErrorHandler> handleResourceErrorHandler(ErrorHandlerException ex) {
        return ResponseEntity.badRequest().body(ex.getErrorHandler());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleResourceBadCrendentials(BadCredentialsException ex) {
        return ResponseEntity.badRequest().body("Credenciales incorrectas");
    }

}
