package com.caronte.caronte.user;

import com.caronte.caronte.plan.Plan;
import com.caronte.caronte.util.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends BaseEntity{

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 15)
    private String telephone;

    // Relationships
    @OneToOne(cascade = CascadeType.ALL)
    private Plan plan;

}
