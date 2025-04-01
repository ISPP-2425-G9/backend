package com.caronte.caronte.admin.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObituaryResponseDTO {

    private Long id;
    private String name;
    private String customImage; 
    private String farewellMessage;
    private String farewellPhrase;

}
