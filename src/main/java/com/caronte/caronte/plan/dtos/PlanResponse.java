package com.caronte.caronte.plan.dtos;

import java.time.LocalDateTime;
import com.caronte.caronte.plan.Plan;
import com.caronte.caronte.plan.PlanType;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanResponse {

    @NotNull
    private PlanType planType;

    private LocalDateTime expiringDate;

    public PlanResponse(LocalDateTime expiringDate, PlanType planType) {
        this.expiringDate = expiringDate;
        this.planType = planType;
    }

    public PlanResponse(Plan plan) throws StripeException{
        this.planType = plan.getPlanType();

        if(plan.isPremium()) {
            Subscription subscription = Subscription.retrieve(plan.getSubscriptionId());
            Long currentPeriodEnd = subscription.getCurrentPeriodEnd();
            this.expiringDate = LocalDateTime.ofEpochSecond(currentPeriodEnd, 0, java.time.ZoneOffset.UTC);
        } else {
            this.expiringDate = null;
        }
    }

}
