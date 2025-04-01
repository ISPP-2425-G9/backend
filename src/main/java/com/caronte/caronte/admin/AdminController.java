package com.caronte.caronte.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.caronte.caronte.admin.DTOs.CertificateResponseDTO;
import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.obituary.Obituary;

@RestController
@RequestMapping("api/admin")
public class AdminController{


    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @GetMapping("/certificates/pending")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllPendingDeathCertificate() {
        List<CertificateResponseDTO> deathCertificates = adminService.getAllPendingCertificates();
        return ResponseEntity.ok(deathCertificates);
    }


    @GetMapping("/certificates/obituaries/{deathCertificateId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllObituariesByDeathCertificate() {
        List<CertificateResponseDTO> deathCertificates = adminService.getAllPendingCertificates();
        return ResponseEntity.ok(deathCertificates);
    }

    @GetMapping("/certificates/messages/{deathCertificateId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllMessagesByDeathCertificate() {
        List<CertificateResponseDTO> deathCertificates = adminService.getAllPendingCertificates();
        return ResponseEntity.ok(deathCertificates);
    }




    




    

}