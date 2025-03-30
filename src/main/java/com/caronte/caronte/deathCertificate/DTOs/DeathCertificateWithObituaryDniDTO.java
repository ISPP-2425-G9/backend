package com.caronte.caronte.deathCertificate.DTOs;

import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.obituary.Obituary;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class DeathCertificateWithObituaryDniDTO {
    @NotBlank(message = "The DNI cannot be blank")
    private String dni;

    @NotBlank(message = "The certificate cannot be blank")
    private DeathCertificate deathCertificate;

    public DeathCertificateWithObituaryDniDTO(Obituary obituary) {
        this.dni = obituary.getCustomer().getDni();
        this.deathCertificate = obituary.getDeathCertificate();
    }
    
}
