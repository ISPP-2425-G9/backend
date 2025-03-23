package com.caronte.caronte.util.exceptions;

public class ResourceNotFound extends RuntimeException {

    public ResourceNotFound(String clazz) {
        super(String.format("%s not found", clazz));
    }
    
    public ResourceNotFound(String clazz, String property, String value) {
        super(String.format("%s with %s:%s not found", clazz, property, value));
    }

    public static ResourceNotFound of(String clazz) {
        return new ResourceNotFound(clazz);
    }

    public static ResourceNotFound of(String clazz, String property, String value) {
        return new ResourceNotFound(clazz, property, value);
    }
}