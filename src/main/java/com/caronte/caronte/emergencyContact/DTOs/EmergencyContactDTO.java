package com.caronte.caronte.emergencyContact.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.caronte.caronte.util.RegexContants.REGEX_EMAIL;
import static com.caronte.caronte.util.RegexContants.REGEX_TELEPHONE;

public record EmergencyContactDTO(
                                Long id,
                                @NotBlank @Size(max = 50, message = "The name must be at most 100 characters long") String name,
                                @NotBlank @Pattern(regexp = REGEX_TELEPHONE, message = "The telephone is not valid") String telephone,
                                @NotBlank @Pattern(regexp = REGEX_EMAIL, message = "The email is not valid") String email) {
}
