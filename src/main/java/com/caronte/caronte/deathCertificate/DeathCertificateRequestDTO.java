package com.caronte.caronte.deathCertificate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeathCertificateRequestDTO {

    @NotBlank(message = "The certificate cannot be blank")
    private String file;

    @Pattern(regexp = "^[\\d]{8}[A-Za-z]$", message = "The DNI must have 8 digits followed by a letter")
    private String dni;

}
