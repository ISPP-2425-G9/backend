package com.caronte.caronte.obituary;

import java.time.LocalDate;
import java.util.Objects;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.imageTemplate.ImageTemplate;
import com.caronte.caronte.obituary.DTOs.ObituraryRequestDto;
import com.caronte.caronte.util.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Obituary extends BaseEntity {

    @Column(length = 37)
    private String name;

    private LocalDate birthDate;

    private LocalDate deathDate;

    @Column(length = 512)
    private String customImageUrl;

    @Column(length = 624)
    private String farewellMessage;

    @Column(length = 90)
    private String farewellPhrase;

    @Column(length = 11)
    private String wordColor;

    @Column(nullable = false)
    private Boolean isMine;

    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ImageTemplate imageTemplate;

    @ManyToOne
    private DeathCertificate deathCertificate;

    @JsonIgnore
    public boolean hasCustomerId(Long customerId){
        return Objects.equals(this.getCustomer().getId(), customerId);
    }
    
    @JsonIgnore
    public void setDefaultWordColorIfNull() {
        if(this.wordColor == null)
            this.wordColor = "0,0,0";
    }

    @JsonIgnore
    public boolean isVerified(){
        return this.getDeathCertificate() != null && this.getDeathCertificate().getIsVerified();
    }

    public void update(ObituraryRequestDto obituraryRequestDto) {
        this.setBirthDate(obituraryRequestDto.getBirthDate());
        this.setName(obituraryRequestDto.getName());
        this.setBirthDate(obituraryRequestDto.getBirthDate());
        this.setDeathDate(obituraryRequestDto.getDeathDate());
        this.setFarewellMessage(obituraryRequestDto.getFarewellMessage());
        this.setFarewellPhrase(obituraryRequestDto.getFarewellPhrase());
        this.setIsMine(obituraryRequestDto.getIsMine());
        this.setWordColor(obituraryRequestDto.getWordColor());
    }
}
