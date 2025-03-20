package com.caronte.caronte.plan;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;

@Getter
public class ChangePlanRequest {

    private String paymentMethodId;
    private PlanType planType;

    @JsonIgnore
    public boolean isPremium() {
        return this.planType == PlanType.PREMIUM;
    }
}
