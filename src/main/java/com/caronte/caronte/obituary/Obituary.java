package com.caronte.caronte.obituary;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.imageTemplate.ImageTemplate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Obituary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "obituary_id", nullable = false, updatable = false)
    private Long obituaryId;

    @Column(nullable = false, length = 2000)
    private String structure;

    @Column(name = "is_mine", nullable = false)
    private Boolean isMine;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "imageTemplate_id", nullable = false)
    private ImageTemplate imageTemplate;
}
