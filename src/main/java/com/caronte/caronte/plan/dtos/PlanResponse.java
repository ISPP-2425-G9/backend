package com.caronte.caronte.plan.dtos;

import java.time.LocalDate;

import com.caronte.caronte.plan.Plan;
import com.caronte.caronte.plan.PlanType;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanResponse {

    @NotNull
    private PlanType planType;

    private LocalDate expiringDate;

    public PlanResponse(LocalDate expiringDate, PlanType planType) {
        this.expiringDate = expiringDate;
        this.planType = planType;
    }

    public PlanResponse(Plan plan){
        this.planType = plan.getPlanType();
        //TODO: añadir el año preguntandoselo a Stripe
        this.expiringDate = LocalDate.of(2030, 3, 30);
    }

}
