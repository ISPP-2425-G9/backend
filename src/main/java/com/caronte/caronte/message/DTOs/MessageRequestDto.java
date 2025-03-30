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

    public List<RecipientDto> getRecipients() {
        return this.recipients != null ? this.recipients : List.of();
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
