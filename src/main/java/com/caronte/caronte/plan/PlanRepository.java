package com.caronte.caronte.plan;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.caronte.caronte.util.LongRepository;

@Repository
public interface PlanRepository extends LongRepository<Plan> {

    @Modifying
    @Query("UPDATE Plan p SET p.planType = :planType WHERE p.id = :id")
    void updatePlanType(Long id, PlanType planType);

}
