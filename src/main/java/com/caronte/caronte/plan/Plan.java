package com.caronte.caronte.plan;

import com.caronte.caronte.util.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Plan extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType;

    private String subscriptionId;

    public static Plan newPlanFree() {
        return new Plan(PlanType.FREE, null);
    }

    public static Plan newPlanPremium(String subscriptionId) {
        return new Plan(PlanType.PREMIUM, subscriptionId);
    }

    public boolean isFree() {
        return this.planType == PlanType.FREE;
    }

    public boolean isPremium() {
        return this.planType == PlanType.PREMIUM;
    }

    @JsonIgnore
    public void cancel() throws StripeException {
        if(isPremium()){
            Subscription.retrieve(this.getSubscriptionId());
        }
    }

}
