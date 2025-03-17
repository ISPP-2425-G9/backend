package com.caronte.caronte.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMessageRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    @NotBlank
    private String code;

    @NotNull
    private Boolean isLastWill;

    private String videoUrl;
    private String image;

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
