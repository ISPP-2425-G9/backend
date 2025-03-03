package com.caronte.caronte.imageTemplate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ImageTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imageTemplate_id", nullable = false, updatable = false)
    private Long imageId;

    @Column(name = "image_url", nullable = false, length = 512)
    private String imageUrl;
}
