package com.caronte.caronte.company;

import com.caronte.caronte.plan.Plan;
import com.caronte.caronte.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Company extends User {

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 10)
    private String zipCode;

    @Column(length = 512)
    private String imageUrl;

    @Column(length = 1024)
    private String description;

    @Column(unique = true, nullable = false, length = 20)
    private String nif;


}
