package com.caronte.caronte.obituary;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.caronte.caronte.util.LongRepository;

@Repository
public interface ObituaryRepository extends LongRepository<Obituary> {
    @Query("SELECT o FROM Obituary o WHERE o.customer.id = :customerId")
    List<Obituary> findByCustomerId(Long customerId);

    @Query("SELECT o FROM Obituary o WHERE o.customer.dni = :customerDni")
    List<Obituary> findByCustomerDni(String customerDni);

    @Query("SELECT o FROM Obituary o WHERE o.deathCertificate.id = :deathCertificateId")
    List<Obituary> findByDeathCertificateId(Long deathCertificateId);
}
