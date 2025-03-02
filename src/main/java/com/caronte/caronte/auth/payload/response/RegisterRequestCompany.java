package com.caronte.caronte.auth.payload.response;

import static com.caronte.caronte.util.RegexContants.REGEX_EMAIL;
import static com.caronte.caronte.util.RegexContants.REGEX_NIF;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestCompany {

    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Pattern(regexp = REGEX_EMAIL, message = "Invalid email format")
    @JsonProperty("email")
    private String id;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotBlank(message = "Telephone is required")
    private String telephone;
    
    @NotBlank(message = "Direction is required for company")
    @JsonProperty("direction")
    private String address;
    
    @NotBlank(message = "City is required for company")
    private String city;
    
    
    @NotBlank(message = "ZipCode is required for company")
    private String zipCode;
    
    @NotBlank(message = "NIF is required for company")
    @Pattern(regexp = REGEX_NIF, message = "Invalid NIF format")
    private String nif;
    
    @JsonProperty("image")
    private String imageUrl;
    
    private String description;
}
