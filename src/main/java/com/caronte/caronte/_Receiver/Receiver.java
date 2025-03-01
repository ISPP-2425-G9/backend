package com.caronte.caronte._Receiver;

import com.caronte.caronte._Obituary.Obituary;

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
public class Receiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receiver_id", nullable = false, updatable = false)
    private Long receiverId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 15, unique = true)
    private String telephone;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "obituary_id")
    private Obituary obituary;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;
}
