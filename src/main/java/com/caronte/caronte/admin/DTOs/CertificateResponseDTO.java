package com.caronte.caronte.admin.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertificateResponseDTO {

    private Long id;
    private String dni; 
    private String name; 
    private String certificateUrl;

    
}
