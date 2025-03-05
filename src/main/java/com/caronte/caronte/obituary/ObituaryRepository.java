package com.caronte.caronte.obituary;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.caronte.caronte.util.LongRepository;

@Repository
public interface ObituaryRepository extends LongRepository<Obituary> {
    @Query("SELECT o FROM Obituary o WHERE o.customer.id = :customerId")
    Iterable<Obituary> findByCustomerId(Long customerId);
}
