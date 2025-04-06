package com.caronte.caronte.admin.DTOs;

import java.util.List;

import com.caronte.caronte.image.Image;

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
