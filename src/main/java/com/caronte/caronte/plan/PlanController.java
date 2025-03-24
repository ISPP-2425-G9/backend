package com.caronte.caronte.plan;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.auth.AuthService;
import com.caronte.caronte.auth.payload.response.JwtResponse;
import com.caronte.caronte.configuration.jwt.JwtUtils;
import com.caronte.caronte.configuration.services.StripeService;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserService;
import com.caronte.caronte.util.exceptions.ResponseThrow;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final AuthService authService;
    private final PlanService planService;
    private final UserService userService;
    private final StripeService stripeService;
    private final JwtUtils jwtUtils;

    public PlanController(AuthService authService, PlanService planService, UserService userService, 
                          StripeService stripeService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.planService = planService;
        this.userService = userService;
        this.stripeService = stripeService;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    @PutMapping("/{userId}")
    public ResponseEntity<?> changePlan(@PathVariable Long userId, @RequestBody ChangePlanRequest changePlanRequest,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        try {
            User user = userService.authorizeUserOrAdmin(userId);
            PlanType actualPlanType = user.getPlan().getPlanType(), newPlanType = changePlanRequest.getPlanType();
            ResponseThrow.checkOrBadRequest(actualPlanType != newPlanType, 
                                            "You have the same plan: " + actualPlanType);
 
            String subscriptionId = changePlanRequest.isPremium() ?
                    stripeService.subscription(changePlanRequest.getPaymentMethodId(), user): null;

            planService.changePlan(user, newPlanType, subscriptionId);
            
            String jwt = jwtUtils.generateJwtToken(userDetailsImpl);
			List<String> roles = userDetailsImpl.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
			String name = authService.getNameById(userDetailsImpl.getId());
            JwtResponse jwtResponse = new JwtResponse(jwt, userDetailsImpl, roles, name);
            return ResponseEntity.ok(jwtResponse);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getBody());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
