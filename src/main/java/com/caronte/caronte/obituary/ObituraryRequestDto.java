package com.caronte.caronte.obituary;

import java.time.LocalDate;
import java.util.List;

import com.caronte.caronte.deathCertificate.DeathCertificateRequestDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObituraryRequestDto {

    @Size(max = 37, message = "The name must be between 0 and 37 characters long")
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate birthDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate deathDate;

    private String customImage; 

    @Size(max = 624, message = "The farewell message must be at most 624 characters long")
    private String farewellMessage;

    @Size(max = 90, message = "The farewell phrase must be at most 90 characters long")
    private String farewellPhrase;

    @NotNull(message = "The 'imageTemplate_id' field cannot be null")
    private Long imageTemplate_id;

    @NotNull(message = "The 'isMine' field cannot be null")
    private Boolean isMine;

    @NotBlank(message = "The wordColor field cannot be empty")
    @Pattern(regexp = "^(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2}),(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2}),(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})$", 
    message = "Color must be in the format 'r,g,b' where r, g and b are integers between 0 and 255")
    private String  wordColor; 

    private DeathCertificateRequestDTO deathCertificate;

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

    public Obituary parse(){
        Obituary obituary = new Obituary();
        obituary.setName(this.name);
        obituary.setBirthDate(this.birthDate);
        obituary.setDeathDate(this.deathDate);
        obituary.setFarewellMessage(this.farewellMessage);
        obituary.setFarewellPhrase(this.farewellPhrase);
        obituary.setIsMine(this.isMine);
        obituary.setWordColor(this.wordColor);
        return obituary;
    }

}
