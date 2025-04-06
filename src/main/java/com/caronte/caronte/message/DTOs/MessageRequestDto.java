package com.caronte.caronte.message.DTOs;

import java.util.List;
import static com.caronte.caronte.util.RegexContants.REGEX_TELEPHONE;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequestDto {

    private Long messageId = null;

    @Size(max = 80, message = "The title message must be at most 80 characters long")
    private String title;

    @Size(max = 2000, message = "The  message must be at most 2000 characters long")
    private String body;

    @Size(max = 5)
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
        @NotBlank(message = "The contact name cannot be empty")
        private String name;

        @Pattern(regexp = REGEX_TELEPHONE, message = "The phone number must contain 9 digits")
        private String telephone;

        @Email(message = "The email is not valid")
        @NotBlank(message = "The email cannot be empty")
        private String email;
    }
}
