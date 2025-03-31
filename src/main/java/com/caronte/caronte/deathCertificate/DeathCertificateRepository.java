package com.caronte.caronte.deathCertificate;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.caronte.caronte.util.LongRepository;

@Repository
public interface DeathCertificateRepository extends LongRepository<DeathCertificate> {

    List<DeathCertificate> getAllCertificatesByIsVerified(boolean isVerified);
    
}
