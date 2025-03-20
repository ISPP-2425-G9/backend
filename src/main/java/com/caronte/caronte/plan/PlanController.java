package com.caronte.caronte.plan;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caronte.caronte.configuration.services.StripeService;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserService;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private PlanService planService;
    private UserService userService;
    private StripeService stripeService;

    public PlanController(PlanService planService, UserService userService, StripeService stripeService) {
        this.planService = planService;
        this.userService = userService;
        this.stripeService = stripeService;
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> changePlan(@PathVariable Long userId, @RequestBody ChangePlanRequest changePlanRequest) {
        try {
            User user = userService.checkIsCurrentUser(userId);
            String subscriptionId = changePlanRequest.isPremium() ?
                    stripeService.subscription(changePlanRequest.getPaymentMethodId(), user): null;
            Plan plan = planService.changePlan(user, changePlanRequest.getPlanType(), subscriptionId);
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
