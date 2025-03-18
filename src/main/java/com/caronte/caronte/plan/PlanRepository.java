package com.caronte.caronte.plan;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.util.LongRepository;

@Repository
public interface PlanRepository extends LongRepository<Plan> {

    @Modifying
    @Query("UPDATE Plan p SET p.expireDate = :expireDate WHERE p.id = :id")
    void updateExpireDate(Long id, String expireDate);

    @Modifying
    @Query("UPDATE Plan p SET p.billingAddress = :billingAddress WHERE p.id = :id")
    void updateBillingAddress(Long id, String billingAddress);

    @Modifying
    @Query("UPDATE Plan p SET p.planType = :planType WHERE p.id = :id")
    void updatePlanType(Long id, PlanType planType);

    @Modifying
    @Query("UPDATE Plan p SET p.expireDate = :expireDate, p.billingAddress = :billingAddress, p.planType = :planType WHERE p.id = :id")
    void updatePlanDetails(Long id, String expireDate, String billingAddress, PlanType planType);

    Optional<Plan> findById(Long id);
}
