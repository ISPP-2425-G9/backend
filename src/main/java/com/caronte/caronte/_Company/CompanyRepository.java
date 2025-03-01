package com.caronte.caronte._Company;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.caronte.caronte.util.LongRepository;


@Repository
public interface CompanyRepository extends LongRepository<Company> {
    Optional<Company> findByNif(String nif);
}
