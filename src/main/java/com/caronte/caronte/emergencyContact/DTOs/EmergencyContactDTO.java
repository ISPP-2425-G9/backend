package com.caronte.caronte.emergencyContact.DTOs;

import static com.caronte.caronte.util.RegexContants.REGEX_EMAIL;
import static com.caronte.caronte.util.RegexContants.REGEX_TELEPHONE;

import com.caronte.caronte.emergencyContact.EmergencyContact;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmergencyContactDTO(
                                Long id,
                                @NotBlank @Size(max = 50, message = "The name must be at most 100 characters long") String name,
                                @NotBlank @Pattern(regexp = REGEX_TELEPHONE, message = "The telephone is not valid") String telephone,
                                @NotBlank @Pattern(regexp = REGEX_EMAIL, message = "The email is not valid") String email) {

            public static EmergencyContactDTO parse(EmergencyContact emergencyContact) {
                return new EmergencyContactDTO(emergencyContact.getId(),
                emergencyContact.getName(),
                emergencyContact.getTelephone(),
                emergencyContact.getEmail());
            }

}
