package com.caronte.caronte.auth.payload.response;

import java.util.Objects;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserChangePasswordRequest {

    @NotBlank
    @Length(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String newPassword;

    @NotBlank
    private String confirmPassword;

    @AssertTrue(message = "La contraseña no coinciden")
    public boolean isEqualsPassword(){
        return Objects.equals(newPassword, confirmPassword);
    }
}
