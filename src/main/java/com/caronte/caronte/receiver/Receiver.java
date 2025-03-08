package com.caronte.caronte.receiver;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.caronte.caronte.message.Message;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.util.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Receiver extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 15, unique = true)
    private String telephone;

    @Column(nullable = false, unique = true)
    private String email;

    // Relationships
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Obituary obituary;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Message message;
}
