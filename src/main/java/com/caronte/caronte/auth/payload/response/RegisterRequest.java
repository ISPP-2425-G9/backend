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
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private static final String REGEX = "^(" + REGEX_DNI + "|" + REGEX_NIF + "|" + REGEX_EMAIL + ")$";
    
    @NotBlank(message = "ID (dni, nif or email) is required")
    @Pattern(regexp = REGEX, message = "No se ha introducido dni, nif o email válido")
    private String id;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotBlank(message = "Telephone is required")
    private String telephone;
    
    @NotBlank(message = "User type is required")
    @Pattern(regexp = "^(CUSTOMER|COMPANY)$", message = "User type must be CUSTOMER or COMPANY")
    private String userType;
    
    // CUSTOMER
    private String dni;
    
    // COMPANY
    private String address;
    private String city;
    private String zipCode;
    private String nif;
    private String description; // OPTIONAL
    private String imageUrl; // OPTIONAL

    // Plan info for CUSTOMER
    private String planTypeValue;
    private String planExpireDate; // YYYY-MM-DD    
    private String planBillingAddress;
}
