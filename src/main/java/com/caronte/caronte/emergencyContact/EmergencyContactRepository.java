package com.caronte.caronte.emergencyContact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {

    @Query("SELECT e FROM EmergencyContact e WHERE e.customer.email = ?1")
    public List<EmergencyContact> findAllByCustomerEmail(String email);


}
