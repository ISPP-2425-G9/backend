package com.caronte.caronte.deathCertificate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeathCertificateWithObituaryDniDTO {
    @NotBlank(message = "El DNI no puede estar en blanco")
    private String dni;

    @NotBlank(message = "El certificado no puede estar en blanco")
    private DeathCertificate deathCertificate;

    public DeathCertificateWithObituaryDniDTO(String dni, DeathCertificate deathCertificate) {
        this.dni = dni;
        this.deathCertificate = deathCertificate;
    }
    
}
