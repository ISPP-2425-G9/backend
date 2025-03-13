package com.caronte.caronte.company;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.caronte.caronte.util.LongRepository;


@Repository
public interface CompanyRepository extends LongRepository<Company> {
    Optional<Company> findByNif(String nif);
    boolean existsByNif(String nif);
    List<Company> findByPlan_PlanType(com.caronte.caronte.plan.PlanType planType);

}


