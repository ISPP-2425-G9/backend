package com.caronte.caronte.customer;

import java.util.Objects;

import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;
import com.caronte.caronte.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer extends User {

    @Column(nullable = false)
    private Boolean isActive;

    @Column(unique = true, nullable = false, length = 20)
    private String dni;

    @JsonIgnore
    public boolean hasDni(String dni) {
        return Objects.equals(this.dni, dni);
    }

    public void update(CustomerUpdateRequest request) {
        this.setEmail(request.getEmail());
        this.setName(request.getFullName());
        this.setTelephone(request.getTelephone());
    }

}
