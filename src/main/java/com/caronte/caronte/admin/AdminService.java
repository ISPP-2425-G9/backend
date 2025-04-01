package com.caronte.caronte.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.admin.DTOs.CertificateResponseDTO;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.customer.CustomerService;
import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.deathCertificate.DeathCertificateRepository;
import com.caronte.caronte.obituary.ObituaryRepository;
import com.caronte.caronte.obituary.ObituaryService;
import com.caronte.caronte.user.UserService;


@Service
public class AdminService {

    private final DeathCertificateRepository deathCertificateRepository;
    private final ObituaryRepository obituaryRepository;
    private final UserService userService;
    private final ObituaryService obituaryService;


    public AdminService(DeathCertificateRepository deathCertificateRepository, 
        ObituaryRepository obituaryRepository, UserService userService, 
        CustomerRepository customerRepository, ObituaryService obituaryService) {

        this.obituaryRepository = obituaryRepository;
        this.deathCertificateRepository = deathCertificateRepository;      
        this.userService = userService;
        this.obituaryService = obituaryService;
    }


    @Transactional
    public List<CertificateResponseDTO> getAllPendingCertificates() {
        userService.authorizeAdmin("User is not admin");
        List<DeathCertificate> pendingCertificates = deathCertificateRepository.getAllCertificatesByIsVerified(false);
        List<CertificateResponseDTO> response = new ArrayList();
        for (DeathCertificate certificate : pendingCertificates) {

            CertificateResponseDTO certificateResponse = new CertificateResponseDTO();
            certificateResponse.setId(certificate.getId());
            certificateResponse.setCertificateUrl(certificate.getUrl());
            Customer customer = obituaryService.getCustomerByCertificateId(certificate.getId());
            certificateResponse.setDni(customer.getDni());
            certificateResponse.setName(customer.getName());
            response.add(certificateResponse);

            
        }
        return response;
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