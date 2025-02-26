package com.caronte.caronte.Message;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 2000)
    private String body;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "is_last_will", nullable = false)
    private Boolean isLastWill;
}
