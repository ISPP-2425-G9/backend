package com.caronte.caronte.util.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.caronte.caronte.util.ErrorHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class, DataIntegrityViolationException.class})
    public ResponseEntity<Map<String,String>> handleResourceException(Exception ex) {
        return ResponseEntity.internalServerError().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ErrorHandlerException.class)
    public ResponseEntity<ErrorHandler> handleResourceErrorHandler(ErrorHandlerException ex) {
        return ResponseEntity.badRequest().body(ex.getErrorHandler());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleResourceBadCrendentials(BadCredentialsException ex) {
        return ResponseEntity.badRequest().body("Credenciales incorrectas");
    }

    
    @ExceptionHandler({ MethodArgumentNotValidException.class, IllegalArgumentException.class })
    public ResponseEntity<Map<String, String>> handleValidationExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();

        if (ex instanceof MethodArgumentNotValidException) {
            ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors()
                    .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        } else {
            errors.put("error", ex.getMessage());
        }

        return ResponseEntity.badRequest().body(errors);
    }

}
