package com.caronte.caronte.plan.dtos;

import com.caronte.caronte.plan.Plan;
import com.caronte.caronte.plan.PlanType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

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
