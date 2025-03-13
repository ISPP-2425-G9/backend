package com.caronte.caronte.imageTemplate;

import com.caronte.caronte.util.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ImageTemplate extends BaseEntity {

    @Column(name = "image_url", nullable = false, length = 512)
    private String imageUrl;
}
