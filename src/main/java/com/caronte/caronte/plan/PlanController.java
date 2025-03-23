package com.caronte.caronte.plan;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.plan.dtos.PlanResponse;
import com.caronte.caronte.user.UserService;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private PlanService planService;
    private UserService userService;

    public PlanController(PlanService planService, UserService userService) {
        this.planService = planService;
        this.userService = userService;
    }

    @PutMapping("/{userId}/premium")
    public Plan setPremiumPlan(@PathVariable Long userId) {
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // if (userService.findCurrentUser().getId() != userId & !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
        //     throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes cambiar el plan de otro usuario");
        // }

        // Long planId = userService.getPlanId(userId);
        // return planService.changePlan(planId, PlanType.PREMIUM);
        return null;
    }

    @PutMapping("/{userId}/free")
    public Plan setFreePlan(@PathVariable Long userId) {
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // if (userService.findCurrentUser().getId() != userId & !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
        //     throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes cambiar el plan de otro usuario");
        // }
        // Long planId = userService.getPlanId(userId);
        // return planService.changePlan(planId, PlanType.FREE);
        return null;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<PlanResponse> getPlan (@PathVariable Long userId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (userService.findCurrentUser().getId() != userId & !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes ver el plan de otro usuario");
        }
        return ResponseEntity.ok(planService.getPlanInfo(userId));
    }
}
