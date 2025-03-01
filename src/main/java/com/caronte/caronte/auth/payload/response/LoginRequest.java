package com.caronte.caronte.auth.payload.response;

import static com.caronte.caronte.util.RegexContants.REGEX_DNI;
import static com.caronte.caronte.util.RegexContants.REGEX_EMAIL;
import static com.caronte.caronte.util.RegexContants.REGEX_NIF;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LoginRequest {

    private static final String REGEX = "^(" + REGEX_DNI + "|" + REGEX_NIF + "|" + REGEX_EMAIL + ")$";
	
	@NotBlank
    @Pattern(regexp = REGEX, message = "No se ha introducido dni, nif o email")
	private String id;

	@NotBlank
	private String password;

}
