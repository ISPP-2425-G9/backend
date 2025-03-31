package com.caronte.caronte.receiver.DTOs;

import static com.caronte.caronte.util.RegexContants.REGEX_TELEPHONE;

import com.caronte.caronte.receiver.Receiver;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiverResponseDTO {

    @Id
    Long id;

    @NotBlank(message = "The contact name cannot be empty")
    @Size(max = 100, message = "The name must be at most 100 characters long")
    String name; 

    @NotNull(message = "The telephone cannot be null")
    @Pattern(regexp = REGEX_TELEPHONE, message = "The phone number must contain 9 numeric digits")
    String telephone;

    @Email(message = "The email is not valid")
    @NotBlank(message = "The email cannot be empty")
    String email;
    
    public static ReceiverResponseDTO parse(Receiver receiver){
        ReceiverResponseDTO receiverResponseDTO = new ReceiverResponseDTO();
        receiverResponseDTO.setId(receiver.getId());
        receiverResponseDTO.setName(receiver.getName());
        receiverResponseDTO.setTelephone(receiver.getTelephone());
        receiverResponseDTO.setEmail(receiver.getEmail());
        return receiverResponseDTO;
    }
}
