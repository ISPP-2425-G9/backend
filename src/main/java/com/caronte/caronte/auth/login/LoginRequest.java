package com.caronte.caronte.auth.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private static final String REGEX_DNI = "((\\d{8}[A-Z])";
    private static final String REGEX_NIF = "([XYZ]\\d{7}[A-Z])";
    private static final String REGEX_GMAIL = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
    private static final String REGEX = "^(" + REGEX_DNI + "|" + REGEX_NIF + "|" + REGEX_GMAIL + ")$";
	
	@NotBlank
    @Pattern(regexp = REGEX)
	private String id;

	@NotBlank
	private String password;

}
