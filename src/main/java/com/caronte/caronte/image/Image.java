package com.caronte.caronte.image;

import com.caronte.caronte.company.Company;
import com.caronte.caronte.message.Message;

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
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false, updatable = false)
    private Long imageId;

    @Column(name = "image_url", nullable = false, length = 512)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = true)
    private Company company;

}
