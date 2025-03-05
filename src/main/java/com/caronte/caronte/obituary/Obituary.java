package com.caronte.caronte.obituary;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
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

    @Column(name = "name", length = 37)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @Column(name = "custom_image_url", length = 512)
    private String customImageUrl;

    @Column(name = "farewell_message", length = 624)
    private String farewellMessage;

    @Column(name = "farewell_phrase", length = 90)
    private String farewellPhrase;

    @Column(name = "is_mine", nullable = false)
    private Boolean isMine;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "imageTemplate_id", nullable = false)
    private ImageTemplate imageTemplate;
}
