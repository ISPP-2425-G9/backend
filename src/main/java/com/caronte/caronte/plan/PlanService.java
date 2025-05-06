package com.caronte.caronte.plan;


import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.user.User;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;


@Service
public class PlanService {

    private PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public Plan changePlan(User user, PlanType planType, String subscriptionId) throws StripeException {
        Plan plan = user.getPlan();
        // If you change premium plan to free, Stripe subscription must be cancel
        if(plan.isPremium() && planType == PlanType.FREE) {
            plan.cancel();
        } else {
            plan.setPlanType(planType);
            plan.setSubscriptionId(subscriptionId);
        }
        return planRepository.save(plan);
    }

    @Transactional
    public LocalDateTime getExpiringDate(User user) throws StripeException {
        Plan plan = user.getPlan();
        if(plan.isPremium()) {
            try {
                Subscription subscription = Subscription.retrieve(plan.getSubscriptionId());
                Long currentPeriodEnd = subscription.getCurrentPeriodEnd();
                return LocalDateTime.ofEpochSecond(currentPeriodEnd, 0, java.time.ZoneOffset.UTC);
            } catch (InvalidRequestException e) {
                plan.setPlanType(PlanType.FREE);
                plan.setSubscriptionId(null);
                planRepository.save(plan);
                return null;
            }

        } else {
            return null;
        }
    }

}
