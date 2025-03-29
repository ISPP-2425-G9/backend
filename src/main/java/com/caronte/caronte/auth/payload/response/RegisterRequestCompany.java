package com.caronte.caronte.auth.payload.response;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyType;
import com.caronte.caronte.plan.Plan;
import static com.caronte.caronte.util.RegexContants.REGEX_EMAIL;
import static com.caronte.caronte.util.RegexContants.REGEX_NIF;
import static com.caronte.caronte.util.RegexContants.REGEX_ZIP_CODE;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestCompany {

    @Length(max=100)
    @NotBlank(message = "El nombre es requerido")
    private String name;

    @Length(max=255)
    @NotBlank(message = "El email es requerido")
    @Pattern(regexp = REGEX_EMAIL, message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password1 es requerido")
    private String password1;

    @NotBlank(message = "Password2 es requerido")
    private String password2;

    @Length(max=15)
    @NotBlank(message = "El teléfono es requerido")
    private String telephone;

    @Length(max=255)
    @NotBlank(message = "La dirección es requerida")
    private String address;

    @Length(max=100)
    @NotBlank(message = "La ciudad es requerida")
    private String city;

    @NotBlank(message = "El código postal es requerido")
    @Pattern(regexp = REGEX_ZIP_CODE, message = "Formato de código postal invalido")
    private String zipCode;

    @NotBlank(message = "El NIF de la compañia es requerido")
    @Pattern(regexp = REGEX_NIF, message = "Formato de NIF invalido")
    private String nif;

    @URL
    @Length(max=512)
    @JsonProperty("image")
    private String imageUrl;

    @Length(max=1024)
    private String description;

    @Enumerated(EnumType.STRING)
    private CompanyType companyType;

    public Company parse(PasswordEncoder passwordEncoder){
        Company company = new Company();
        company.setAddress(this.getAddress());
        company.setCity(this.getCity());
        company.setZipCode(this.getZipCode());
        company.setNif(this.getNif());
        company.setDescription(this.getDescription());
        company.setImageUrl(this.getImageUrl());
        company.setName(this.getName());
        company.setEmail(this.getEmail());
        company.setPassword(passwordEncoder.encode(this.getPassword1()));
        company.setTelephone(this.getTelephone());
        company.setPlan(Plan.newPlanFree());
        company.setCompanyType(this.getCompanyType());
      
        return company;

    }
}
