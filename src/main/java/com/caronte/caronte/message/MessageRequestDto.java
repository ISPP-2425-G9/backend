package com.caronte.caronte.message;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequestDto {

    private String title;

    private String body;

    private List<String> customImages;

    @NotNull
    private Boolean isLastWill = false;
    

    private List<RecipientDto> recipients;

    @Getter
    @Setter
    public static class RecipientDto {
        private String name;
        @NotBlank
        private String telephone;
        @NotBlank
        private String email;
    }
}
