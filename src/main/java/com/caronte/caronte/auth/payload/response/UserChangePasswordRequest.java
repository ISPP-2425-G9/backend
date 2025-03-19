package com.caronte.caronte.auth.payload.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserChangePasswordRequest {

    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}
