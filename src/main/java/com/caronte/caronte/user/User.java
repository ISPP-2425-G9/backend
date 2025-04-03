package com.caronte.caronte.user;

import java.time.LocalDateTime;

import com.caronte.caronte.plan.Plan;
import com.caronte.caronte.util.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends BaseEntity{

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 15)
    private String telephone;

    // Relationships
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private Plan plan;


    @JsonIgnore
    public LocalDateTime getExpiringDate() throws StripeException{
        if(this.getPlan().isPremium()) {
            Subscription subscription = Subscription.retrieve(plan.getSubscriptionId());
            Long currentPeriodEnd = subscription.getCurrentPeriodEnd();
            return LocalDateTime.ofEpochSecond(currentPeriodEnd, 0, java.time.ZoneOffset.UTC);
        } else {
            return null;
        }
    }
}
