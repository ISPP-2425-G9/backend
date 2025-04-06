package com.caronte.caronte.admin.DTOs;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidCertificateRequestDTO {
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate deathDate;
    
    
}
