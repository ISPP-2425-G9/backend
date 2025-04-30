package com.caronte.caronte.customer;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.caronte.caronte.util.LongRepository;

@Repository
public interface CustomerRepository extends LongRepository<Customer>{
    Optional<Customer> findByDni(String dni);
    boolean existsByDni(String dni);
    Optional<Customer> findByEmail(String email);

    List<Customer> findByDniNot(String dni);
}
