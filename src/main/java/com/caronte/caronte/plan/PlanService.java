package com.caronte.caronte.plan;

import com.caronte.caronte.plan.dtos.PlanResponse;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

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
        Optional<Plan> plan = planRepository.findById(id);
        if(plan.isPresent()){
            plan.get().setPlanType(planType);
            return planRepository.save(plan.get());
        } throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan con ID " + id + " no encontrado");
    }

    public PlanResponse getPlanInfo(Long userId){
        Optional<User> user = userRepository.findById(userId);
        System.out.println(user);
        if (user.isPresent()){
            Plan plan = user.get().getPlan();
            PlanResponse planResponse = new PlanResponse(plan);
            return planResponse;
        }
        return null;
    }
}
