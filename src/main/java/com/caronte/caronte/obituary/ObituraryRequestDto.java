package com.caronte.caronte.obituary;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

import com.caronte.caronte.deathCertificate.DeathCertificateRequestDTO;

@Getter
@Setter
public class ObituraryRequestDto {

    @Size(max = 37, message = "The name must be between 0 and 37 characters long")
    private String name;

    @Pattern(regexp = "^(|\\d{2}/\\d{2}/\\d{4})$", message = "The date format must be dd/MM/yyyy or an empty string")
    private String birthDate;

    private String deathDate;

    private String customImage; 

    @Size(max = 624, message = "The farewell message must be at most 624 characters long")
    private String farewellMessage;

    @Size(max = 90, message = "The farewell phrase must be at most 90 characters long")
    private String farewellPhrase;

    @NotNull(message = "The 'imageTemplate_id' field cannot be null")
    private Long imageTemplate_id;

    @NotNull(message = "The 'isMine' field cannot be null")
    @Pattern(regexp = "^(true|false)$", message = "The 'isMine' field must be either 'true' or 'false'")
    private String isMine;

    private DeathCertificateRequestDTO deathCertificate;

    @AssertTrue(message = "The deathDate field must be null or empty")
    public boolean isDeathDateValid() {
        return deathDate == null || deathDate.trim().isEmpty();
    }

    private List<@Valid ContactDto> contacts;

    @Getter
    @Setter
    public static class ContactDto {
        @NotBlank(message = "The contact name cannot be empty")
        private String name;

        @Pattern(regexp = "\\d{9,15}", message = "The phone number must contain between 9 and 15 numeric digits")
        private String phone;

        @Email(message = "The email is not valid")
        @NotBlank(message = "The email cannot be empty")
        private String email;
    }



}
