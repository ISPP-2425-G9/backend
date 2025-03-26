package com.caronte.caronte.emergencyContact.DTOs;

import jakarta.validation.constraints.NotBlank;

public record EmergencyContactDTO(Long id,
                                @NotBlank String name,
                                @NotBlank String telephone,
                                @NotBlank String email) {
}
