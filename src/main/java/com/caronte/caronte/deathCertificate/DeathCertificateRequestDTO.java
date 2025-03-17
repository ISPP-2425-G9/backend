package com.caronte.caronte.deathCertificate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeathCertificateRequestDTO {

    @NotBlank(message = "El certificado no puede estar en blanco")
    private String certificate;

    @Pattern(regexp = "^[\\d]{8}[A-Za-z]$", message = "El DNI debe tener 8 dígitos seguidos de una letra")
    private String dni;

}
