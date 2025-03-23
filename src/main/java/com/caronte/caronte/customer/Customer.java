package com.caronte.caronte.customer;

import com.caronte.caronte.user.User;

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



}
