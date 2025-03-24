package com.caronte.caronte.deathCertificate;

import com.caronte.caronte.util.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DeathCertificate extends BaseEntity{

    @Column(nullable = false, length = 512)
    private String url; 

    @Column(nullable = false)
    private Boolean isVerified;

    public static DeathCertificate newDeathCertificate(String url) {
        DeathCertificate deathCertificate = new DeathCertificate();
        deathCertificate.setUrl(url);
        deathCertificate.setIsVerified(false);
        return deathCertificate;
    }
}
