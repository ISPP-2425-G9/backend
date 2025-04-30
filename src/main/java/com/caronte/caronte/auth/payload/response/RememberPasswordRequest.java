package com.caronte.caronte.auth.payload.response;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record RememberPasswordRequest(
    @NotBlank(message = "El email no puede estar en blanco") String email, 
    @NotBlank(message = "La contraseña no puede estar en blanco") 
    @Length(min = 6, message = "La contraseña debe tener mínimo 6 caractéres") String password,
    @NotBlank(message = "Debes de introducir el código de verificación") String code) {
}
