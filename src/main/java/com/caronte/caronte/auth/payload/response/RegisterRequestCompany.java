package com.caronte.caronte.auth.payload.response;

import static com.caronte.caronte.util.RegexContants.REGEX_EMAIL;
import static com.caronte.caronte.util.RegexContants.REGEX_NIF;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.plan.Plan;
import com.caronte.caronte.plan.PlanType;

import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @JsonProperty("direction")
    private String address;
    
    @Length(max=100)
    @NotBlank(message = "La ciudad es requerida")
    private String city;
    
    @Length(max=10)
    @NotBlank(message = "El código postal es requerido")
    private String zipCode;
    
    @Length(max=20)
    @NotBlank(message = "El NIF de la compañia es requerido")
    @Pattern(regexp = REGEX_NIF, message = "Formato de NIF invalido")
    private String nif;
    
    @Length(max=512)
    @JsonProperty("image")
    private String imageUrl;
    
    @Length(max=1024)
    private String description;

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
        
        Plan plan = new Plan();
        plan.setPlanType(PlanType.FREE);
        plan.setExpireDate(null);
        plan.setBillingAddress(null);
        company.setPlan(plan);

        return company;

    }
}
