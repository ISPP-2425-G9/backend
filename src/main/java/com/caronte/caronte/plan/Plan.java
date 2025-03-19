package com.caronte.caronte.plan;

import com.caronte.caronte.util.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Plan extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType;

    private String subscriptionId;

    public static Plan newFreePlan() {
        Plan plan = new Plan();
        plan.setPlanType(PlanType.FREE);
        return plan;
    }

}
