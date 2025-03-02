package com.caronte.caronte.auth.payload.response;

import static com.caronte.caronte.util.RegexContants.REGEX_EMAIL;
import static com.caronte.caronte.util.RegexContants.REGEX_DNI;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestCustomer {

    @NotBlank(message = "Full name is required")
    @JsonProperty("full_name")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Pattern(regexp = REGEX_EMAIL, message = "Invalid email format")
    @JsonProperty("email")
    private String id;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotBlank(message = "Telephone is required")
    private String telephone;
    
    @NotBlank(message = "DNI is required for customer")
    @Pattern(regexp = REGEX_DNI, message = "Invalid DNI format")
    private String dni;
    

}
