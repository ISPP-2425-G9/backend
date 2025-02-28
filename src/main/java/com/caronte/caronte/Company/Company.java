package com.caronte.caronte.Company;

import com.caronte.caronte.User.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Company extends User {

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(name = "zip_code", nullable = false, length = 10)
    private String zipCode;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(length = 1024)
    private String description;

    @Column(unique = true, nullable = false, length = 20)
    private String nif;
}
