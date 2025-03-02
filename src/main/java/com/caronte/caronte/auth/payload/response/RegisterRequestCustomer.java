package com.caronte.caronte.auth.payload.response;

import static com.caronte.caronte.util.RegexContants.REGEX_EMAIL;

import java.time.LocalDate;

import static com.caronte.caronte.util.RegexContants.REGEX_DNI;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.plan.Plan;
import com.caronte.caronte.plan.PlanType;

import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
public class RegisterRequestCustomer {

    @NotBlank(message = "El nombre completo es requerido")
    @JsonProperty("full_name")
    private String name;
    
    @NotBlank(message = "El email es requerido")
    @Pattern(regexp = REGEX_EMAIL, message = "Invalid email format")
    @JsonProperty("email")
    private String id;
    
    @NotBlank(message = "Password1 es requerida")
    private String password1;
    
    @NotBlank(message = "Password2 es requerida")
    private String password2;
    
    @NotBlank(message = "El teléfono es requerido")
    private String telephone;
    
    @NotBlank(message = "El DNI del usuario es requerido")
    @Pattern(regexp = REGEX_DNI, message = "Invalid DNI format")
    private String dni;
    
    public Customer parse(PasswordEncoder passwordEncoder) {
        Customer customer = new Customer();
        customer.setDni(this.getDni());
        customer.setIsActive(true);
        customer.setName(this.getName());
        customer.setEmail(this.getId());
        customer.setPassword(passwordEncoder.encode(this.getPassword1()));
        customer.setTelephone(this.getTelephone());

        Plan plan = new Plan();
        plan.setPlanType(PlanType.FREE);
        plan.setExpireDate(null);
        plan.setBillingAddress(null);
        customer.setPlan(plan);
        return customer;
    }
}
