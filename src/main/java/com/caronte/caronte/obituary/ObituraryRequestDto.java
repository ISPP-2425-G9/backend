package com.caronte.caronte.obituary;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ObituraryRequestDto {

    private String name;

    private String birthDate; 

    private String deathDate;

    private String customImage; 

    private String farewellMessage;

    private String farewellPhrase;

    private Long imageTemplate_id;

    @NotNull(message = "El campo 'isMine' no puede ser nulo")
    private Boolean isMine;

    private List<@Valid ContactDto> contacts;

    @Getter
    @Setter
    public static class ContactDto {
        @NotBlank(message = "El nombre del contacto no puede estar vacío")
        private String name;

        @Pattern(regexp = "\\d{9,15}", message = "El teléfono debe contener entre 9 y 15 dígitos numéricos")
        private String phone;

        @Email(message = "El email no es válido")
        @NotBlank(message = "El email no puede estar vacío")
        private String email;
    }
    
}
