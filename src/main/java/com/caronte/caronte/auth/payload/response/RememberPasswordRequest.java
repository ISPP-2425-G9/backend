package com.caronte.caronte.auth.payload.response;

import jakarta.validation.constraints.NotBlank;

public record RememberPasswordRequest(
    @NotBlank(message = "El email no puede estar en blanco") String email, 
    @NotBlank(message = "La contraseña no puede estar en blanco") String password,
    @NotBlank(message = "Debes de introducir el código de verificación") String code) {
}
