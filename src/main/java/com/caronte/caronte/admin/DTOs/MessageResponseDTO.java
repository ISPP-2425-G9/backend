package com.caronte.caronte.admin.DTOs;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponseDTO {

    private Long id;
    private String title;
    private String body; 

    private List<String> images; 
    
}
