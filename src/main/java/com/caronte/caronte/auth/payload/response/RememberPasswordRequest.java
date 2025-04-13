package com.caronte.caronte.auth.payload.response;

import com.caronte.caronte.util.RegexContants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RememberPasswordRequest(
    @Email(regexp = RegexContants.REGEX_EMAIL) String email, 
    @NotBlank String code) {
}
