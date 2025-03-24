package com.caronte.caronte.plan;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.plan.dtos.PlanResponse;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserRepository;
import com.caronte.caronte.util.exceptions.ResourceNotFound;

@Service
public class PlanService {

    private PlanRepository planRepository;

    private UserRepository userRepository;

    public PlanService(PlanRepository planRepository, UserRepository userRepository) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Plan changePlan(Long id, PlanType planType){
        Plan plan = planRepository.findById(id).orElseThrow(() -> ResourceNotFound.of("Plan", "ID", id));
        plan.setPlanType(planType);
        return planRepository.save(plan);
    }

    public PlanResponse getPlanInfo(Long userId){
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()){
            Plan plan = user.get().getPlan();
            PlanResponse planResponse = new PlanResponse(plan);
            return planResponse;
        }
        return null;
    }
}
