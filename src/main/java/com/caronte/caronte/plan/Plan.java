package com.caronte.caronte.plan;

import com.caronte.caronte.util.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
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

}
