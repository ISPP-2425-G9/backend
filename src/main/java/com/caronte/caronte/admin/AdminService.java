package com.caronte.caronte.admin;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.deathCertificate.DeathCertificateRepository;
import com.caronte.caronte.obituary.ObituaryRepository;
import com.caronte.caronte.user.UserService;


@Service
public class AdminService {

    private final DeathCertificateRepository deathCertificateRepository;
    private final ObituaryRepository obituaryRepository;
    private final UserService userService;


    public AdminService(DeathCertificateRepository deathCertificateRepository, ObituaryRepository obituaryRepository, UserService userService) {
        this.obituaryRepository = obituaryRepository;
        this.deathCertificateRepository = deathCertificateRepository;
        this.userService = userService;
    }


    @Transactional
    public List<DeathCertificate> getAllPendingCertificates() {
        userService.authorizeAdmin("User is not admin");
        return deathCertificateRepository.getAllCertificatesByIsVerified(false);
    }

    @Transactional
    public DeathCertificate getCertificateById(Long id) {
        return deathCertificateRepository.findById(id).orElse(null);
    }

    @Transactional
    public void verifyCertificate(Long id) {
        DeathCertificate deathCertificate = deathCertificateRepository.findById(id).orElse(null);
        if (deathCertificate != null) {
            deathCertificate.setIsVerified(true);
            deathCertificateRepository.save(deathCertificate);
        }
    }


    
    




}