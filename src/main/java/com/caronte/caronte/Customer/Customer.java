package com.caronte.caronte.Customer;

import java.util.List;

import com.caronte.caronte.EmergencyContact.EmergencyContact;
import com.caronte.caronte.Message.Message;
import com.caronte.caronte.Obituary.Obituary;
import com.caronte.caronte.Plan.Plan;
import com.caronte.caronte.User.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer extends User {

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(unique = true, nullable = false, length = 20)
    private String dni;

    // Relationships
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id")
    private Plan plan;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmergencyContact> emergencyContacts;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Obituary> obituaries;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

}
