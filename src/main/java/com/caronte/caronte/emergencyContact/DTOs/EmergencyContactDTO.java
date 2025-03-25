package com.caronte.caronte.emergencyContact.DTOs;

import com.caronte.caronte.emergencyContact.EmergencyContact;
import jakarta.validation.constraints.NotBlank;

public record EmergencyContactDTO(@NotBlank String name,
                                  @NotBlank String telephone,
                                  @NotBlank String email) {

}
