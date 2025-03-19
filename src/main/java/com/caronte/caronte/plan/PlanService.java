package com.caronte.caronte.plan;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class PlanService {

    private PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Transactional
    public Plan changePlan(Long id, PlanType planType){
        Optional<Plan> plan = planRepository.findById(id);
        if(plan.isPresent()){
            plan.get().setPlanType(planType);
            return planRepository.save(plan.get());
        } throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan con ID " + id + " no encontrado");
    }
}
