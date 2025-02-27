package com.caronte.caronte.Obituary;

import java.util.List;

import com.caronte.caronte.Customer.Customer;
import com.caronte.caronte.Receiver.Receiver;

import jakarta.persistence.*;
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

    @OneToMany(mappedBy = "obituary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Receiver> receivers;
}
