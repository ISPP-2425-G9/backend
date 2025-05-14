package com.caronte.caronte.plan;


import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caronte.caronte.auth.payload.response.JwtResponse;
import com.caronte.caronte.configuration.jwt.JwtUtils;
import com.caronte.caronte.configuration.services.StripeService;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.plan.DTOs.ChangePlanRequest;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserService;
import com.caronte.caronte.util.exceptions.ResponseThrow;
import com.stripe.exception.StripeException;


@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;
    private final UserService userService;
    private final StripeService stripeService;
    private final JwtUtils jwtUtils;

    public PlanController(PlanService planService, UserService userService, 
                          StripeService stripeService, JwtUtils jwtUtils) {
        this.planService = planService;
        this.userService = userService;
        this.stripeService = stripeService;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    @PutMapping("/{userId}")
    public ResponseEntity<?> changePlan(@PathVariable Long userId, @RequestBody ChangePlanRequest changePlanRequest,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) throws StripeException {
        User user = userService.authorizeUserOrAdmin(userId);
        PlanType actualPlanType = user.getPlan().getPlanType(), newPlanType = changePlanRequest.getPlanType();
        ResponseThrow.checkOrBadRequest(actualPlanType != newPlanType, "You have the same plan: " + actualPlanType);

        String subscriptionId = changePlanRequest.isPremium() ?
                stripeService.subscription(changePlanRequest.getPaymentMethodId(), user): null;
        
        planService.changePlan(user, newPlanType, subscriptionId);
        
        String jwt = jwtUtils.generateJwtToken(userDetailsImpl);
        user = userService.findCurrentUser();
        LocalDateTime expiredDate = planService.getExpiringDate(user);
        JwtResponse jwtResponse = new JwtResponse(jwt, user, expiredDate);
        return ResponseEntity.ok(jwtResponse);
    }

}
