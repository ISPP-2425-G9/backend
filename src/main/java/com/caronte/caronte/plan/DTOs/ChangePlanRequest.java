package com.caronte.caronte.plan.DTOs;

import com.caronte.caronte.plan.PlanType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChangePlanRequest {

    private String paymentMethodId;

    @NotNull
    private PlanType planType;

    @JsonIgnore
    public boolean isPremium() {
        return this.planType == PlanType.PREMIUM;
    }
}
