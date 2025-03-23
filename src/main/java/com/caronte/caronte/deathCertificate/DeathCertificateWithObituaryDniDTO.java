package com.caronte.caronte.deathCertificate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeathCertificateWithObituaryDniDTO {
    @NotBlank(message = "The DNI cannot be blank")
    private String dni;

    @NotBlank(message = "The certificate cannot be blank")
    private DeathCertificate deathCertificate;

    public DeathCertificateWithObituaryDniDTO(String dni, DeathCertificate deathCertificate) {
        this.dni = dni;
        this.deathCertificate = deathCertificate;
    }
    
}
