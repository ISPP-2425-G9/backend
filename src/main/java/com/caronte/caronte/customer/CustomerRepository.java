package com.caronte.caronte.customer;


import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.caronte.caronte.util.LongRepository;

@Repository
public interface CustomerRepository extends LongRepository<Customer>{
    Optional<Customer> findByDni(String dni);
}
