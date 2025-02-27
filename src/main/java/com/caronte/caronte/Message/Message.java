package com.caronte.caronte.Message;

import java.util.List;

import com.caronte.caronte.Customer.Customer;
import com.caronte.caronte.Image.Image;
import com.caronte.caronte.Receiver.Receiver;
import com.caronte.caronte.Video.Video;

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

    // Relationships
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Receiver> receivers;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Video> videos;
}
