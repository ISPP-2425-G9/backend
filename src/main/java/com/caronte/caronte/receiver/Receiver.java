package com.caronte.caronte.receiver;

import com.caronte.caronte.message.Message;
import com.caronte.caronte.obituary.Obituary;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "receiver",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"obituary_id", "telephone"}),
        @UniqueConstraint(columnNames = {"obituary_id", "email"}),
        @UniqueConstraint(columnNames = {"message_id", "telephone"}),
        @UniqueConstraint(columnNames = {"message_id", "email"})
    }
)
public class Receiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receiver_id", nullable = false, updatable = false)
    private Long receiverId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 15)
    private String telephone;

    @Column(nullable = false, length = 255)
    private String email;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "obituary_id")
    private Obituary obituary;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;
}
