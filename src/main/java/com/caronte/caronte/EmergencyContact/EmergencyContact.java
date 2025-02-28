package com.caronte.caronte.EmergencyContact;

import com.caronte.caronte.Customer.Customer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EmergencyContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emergency_contact_id", nullable = false, updatable = false)
    private Long emergencyContactId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 15, unique = true)
    private String telephone;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Customer customer;
}
