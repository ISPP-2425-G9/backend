package com.caronte.caronte.plan;


import org.springframework.stereotype.Service;

import com.caronte.caronte.user.User;
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
        // Si lo que se realiza es una cancelación del plan premium, se debe de cancelar la suscripción en Stripe
        if(plan.isPremium() && planType == PlanType.FREE) {
            Subscription subscription = Subscription.retrieve(plan.getSubscriptionId());
            subscription.cancel();
        }
        plan.setPlanType(planType);
        plan.setSubscriptionId(subscriptionId);
        return planRepository.save(plan);

    }
}
