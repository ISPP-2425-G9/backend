package com.caronte.caronte.obituary;

import java.time.LocalDate;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.imageTemplate.ImageTemplate;
import com.caronte.caronte.util.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Obituary extends BaseEntity {

    @Column(length = 37)
    private String name;

    private LocalDate birthDate;

    private LocalDate deathDate;

    @Column(length = 512)
    private String customImageUrl;

    @Column(length = 624)
    private String farewellMessage;

    @Column(length = 90)
    private String farewellPhrase;

    @Column(length = 11)
    private String wordColor;

    @Column(nullable = false)
    private Boolean isMine;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ImageTemplate imageTemplate;

    @ManyToOne
    private DeathCertificate deathCertificate;
}
