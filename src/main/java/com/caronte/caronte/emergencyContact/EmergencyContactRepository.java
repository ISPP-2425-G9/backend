package com.caronte.caronte.emergencyContact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {

    @Query("SELECT e FROM EmergencyContact e WHERE e.customer.email = ?1")
    public List<EmergencyContact> findAllByCustomerEmail(String email);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EmergencyContact e WHERE e.telephone = ?1 AND e.customer.email = ?2")
    public boolean existsByTelephoneAndCustomer(String telephone, String customerEmail);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EmergencyContact e WHERE e.email = ?1 AND e.customer.email = ?2")
    public boolean existsByEmailAndCustomer(String email, String customerEmail);


}
