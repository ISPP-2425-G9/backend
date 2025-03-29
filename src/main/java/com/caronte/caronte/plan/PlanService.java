package com.caronte.caronte.plan;


import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.caronte.caronte.plan.dtos.PlanResponse;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserRepository;
import com.caronte.caronte.util.exceptions.ResourceNotFound;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;


@Service
public class PlanService {

    private PlanRepository planRepository;

    private UserRepository userRepository;

    public PlanService(PlanRepository planRepository, UserRepository userRepository) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
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

    public PlanResponse getPlanInfo(Long userId) throws StripeException{
        User user = userRepository.findById(userId).orElseThrow();
        Plan plan = user.getPlan();
        PlanResponse planResponse = new PlanResponse(plan);
        return planResponse;
    }
}
