package com.caronte.caronte.auth.payload.response;

import static com.caronte.caronte.util.RegexContants.REGEX_EMAIL;
import static com.caronte.caronte.util.RegexContants.REGEX_TELEPHONE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class CustomerUpdateRequest {

	@NotBlank
	@Pattern(regexp = REGEX_EMAIL, message = "The email is not valid")
    private String email;

	@NotBlank
	private String fullName;

	@NotBlank
	@Pattern(regexp = REGEX_TELEPHONE, message = "The telephone is not valid")
	private String telephone;
}
