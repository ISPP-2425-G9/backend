package com.caronte.caronte.plan;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.caronte.caronte.company.CompanyDTO;
import com.caronte.caronte.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (userService.findCurrentUser().getId() != userId & !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes cambiar el plan de otro usuario");
        }

        Long planId = userService.getPlanId(userId);
        return planService.changePlan(planId, PlanType.PREMIUM);
    }

    @PutMapping("/{userId}/free")
    public Plan setFreePlan(@PathVariable Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (userService.findCurrentUser().getId() != userId & !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes cambiar el plan de otro usuario");
        }
        Long planId = userService.getPlanId(userId);
        return planService.changePlan(planId, PlanType.FREE);
    }
}
