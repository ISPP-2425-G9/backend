package com.caronte.caronte.Obituary;

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
}
