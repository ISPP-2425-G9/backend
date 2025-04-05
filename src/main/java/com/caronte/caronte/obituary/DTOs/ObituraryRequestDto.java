package com.caronte.caronte.obituary.DTOs;

import static com.caronte.caronte.util.RegexContants.*;

import java.time.LocalDate;
import java.util.List;

import com.caronte.caronte.deathCertificate.DTOs.DeathCertificateRequestDTO;
import com.caronte.caronte.obituary.Obituary;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
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

    @Pattern(regexp = REGEX_RGB, message = "Color must be in the format 'r,g,b' where r, g and b are integers between 0 and 255")
    private String  wordColor; 

    private DeathCertificateRequestDTO deathCertificate;

    private List<@Valid ContactDto> contacts;
    
    @Getter
    @Setter
    public static class ContactDto {
        @NotBlank(message = "The contact name cannot be empty")
        private String name;

        @Pattern(regexp = REGEX_TELEPHONE, message = "The phone number must contain 9 digits")
        private String phone;

        @Email(message = "The email is not valid")
        @NotBlank(message = "The email cannot be empty")
        private String email;
    }

    private String paymentMethodId;

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

    @JsonIgnore
    public void setDefaultWordColorIfNull() {
        if(this.wordColor == null)
            this.wordColor = "0,0,0";
    }

}
