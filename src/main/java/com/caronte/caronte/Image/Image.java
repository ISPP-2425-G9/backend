package com.caronte.caronte.Image;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false, updatable = false)
    private Long imageId;

    @Column(name = "image_url", nullable = false, length = 512)
    private String imageUrl;
}
