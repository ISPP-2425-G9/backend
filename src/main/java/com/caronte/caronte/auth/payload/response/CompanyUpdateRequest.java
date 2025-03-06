package com.caronte.caronte.auth.payload.response;

import static com.caronte.caronte.util.RegexContants.REGEX_EMAIL;
import static com.caronte.caronte.util.RegexContants.REGEX_TELEPHONE;
import static com.caronte.caronte.util.RegexContants.REGEX_ZIPCODE;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class CompanyUpdateRequest {

	@NotBlank
	private String name;

	@NotBlank
	private String password;

	@NotBlank
	private String address;

	@NotBlank
	private String city;

	@NotBlank
	@Pattern(regexp = REGEX_ZIPCODE, message = "The zip code is not valid")
	private String zipCode;

	@NotBlank
	@Pattern(regexp = REGEX_EMAIL, message = "The email is not valid")
	private String email;

	@NotBlank
	@Pattern(regexp = REGEX_TELEPHONE, message = "The telephone is not valid")
	private String telephone;

	private String imageUrl;

	private String Description;
}
