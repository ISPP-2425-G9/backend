package com.caronte.caronte.util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResourceNotFound extends ResponseStatusException {

    public ResourceNotFound(String clazz) {
        super(HttpStatus.NOT_FOUND, String.format("%s not found", clazz));
    }
    
    public ResourceNotFound(String clazz, String property, Object value) {
        super(HttpStatus.NOT_FOUND, String.format("%s with %s:%s not found", clazz, property, value));
    }

    public static ResourceNotFound of(String clazz) {
        return new ResourceNotFound(clazz);
    }

    public static ResourceNotFound of(String clazz, String property, Object value) {
        return new ResourceNotFound(clazz, property, value);
    }
}