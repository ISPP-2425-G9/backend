package com.caronte.caronte.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ErrorHandler {

    private Map<String, List<String>> errors;

    public static ErrorHandler catchError(BindingResult bindingResult) {
        Map<String, List<String>> errors = new HashMap<>();
        
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.computeIfAbsent(error.getField(), _ -> new ArrayList<>()).add(error.getDefaultMessage());
            }
        }
        return new ErrorHandler(errors);
    }

    public void addError(String field, String message) {
        errors.computeIfAbsent(field, _ -> new ArrayList<>()).add(message);
    }

    @JsonIgnore
    public boolean isEmpty(){
        return this.errors.isEmpty();
    }

    public boolean hasErrors(){
        return !isEmpty();
    }
    
    public Map<String, List<String>> getErrors() {
        return errors;
    }
}