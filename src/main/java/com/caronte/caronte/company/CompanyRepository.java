package com.caronte.caronte.company;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.caronte.caronte.plan.PlanType;
import com.caronte.caronte.util.LongRepository;


@Repository
public interface CompanyRepository extends LongRepository<Company> {
    Optional<Company> findByNif(String nif);
    boolean existsByNif(String nif);
    List<Company> findByPlan_PlanType(PlanType planType);

    @Query("""
    SELECT c FROM Company c
    WHERE c.plan.planType = 'PREMIUM'
    AND c.nif <> 'anonimo'
    AND (:city IS NULL OR LOWER(c.city) LIKE LOWER(CONCAT('%', :city, '%')))
    AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
    AND (:companyType IS NULL OR c.companyType = :companyType)
    """)
    Page<Company> findPremiumCompaniesFiltered(String city, String name, CompanyType companyType, Pageable pageable);

    List<Company> findByNifNot(String nif);

}


