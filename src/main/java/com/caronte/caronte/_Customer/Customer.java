package com.caronte.caronte._Customer;

import com.caronte.caronte._Plan.Plan;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
    @JoinColumn(name = "plan_id")
    private Plan plan;

}
