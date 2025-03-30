package com.caronte.caronte.emergencyContact;

import java.util.Objects;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.emergencyContact.DTOs.EmergencyContactDTO;
import com.caronte.caronte.util.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EmergencyContact extends BaseEntity {
    
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 15)
    private String telephone;

    @Column(nullable = false)
    private String email;

    // Relationships
    @ManyToOne
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    public EmergencyContact(EmergencyContactDTO emergencyContactDTO, Customer customer) {
        this.name = emergencyContactDTO.name();
        this.telephone = emergencyContactDTO.telephone();
        this.email = emergencyContactDTO.email();
        this.customer = customer;
    }

    @JsonIgnore
    public boolean hasCustomerEmail(String email) {
        return Objects.equals(this.getCustomer().getEmail(), email);
    }
    
    @JsonIgnore
    public boolean hasTelephone(String telephone) {
        return Objects.equals(this.getTelephone(), telephone);
    }

    @JsonIgnore
    public boolean hasEmail(String email) {
        return Objects.equals(this.getEmail(), email);
    }

    public void update(EmergencyContactDTO emergencyContactDTO){
        this.setName(emergencyContactDTO.name());
        this.setTelephone(emergencyContactDTO.telephone());
        this.setEmail(emergencyContactDTO.email());
    }
}
