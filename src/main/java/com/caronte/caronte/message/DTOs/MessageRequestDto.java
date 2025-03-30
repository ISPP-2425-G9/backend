package com.caronte.caronte.message.DTOs;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

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

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    public void setRecipients(List<RecipientDto> recipients) {
        this.recipients = (recipients != null) ? recipients : List.of();
    }

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
