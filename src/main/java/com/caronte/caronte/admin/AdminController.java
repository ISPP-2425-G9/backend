package com.caronte.caronte.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.caronte.caronte.admin.DTOs.CertificateResponseDTO;
import com.caronte.caronte.admin.DTOs.MessageResponseDTO;
import com.caronte.caronte.admin.DTOs.ObituaryResponseDTO;
import com.caronte.caronte.admin.DTOs.ValidCertificateRequestDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/admin/certificates")
public class AdminController{


    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @GetMapping("pending")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllPendingDeathCertificate() {
        List<CertificateResponseDTO> deathCertificates = adminService.getAllPendingCertificates();
        return ResponseEntity.ok(deathCertificates);
    }


    @GetMapping("/obituaries/{deathCertificateId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllObituariesByDeathCertificate(@PathVariable Long deathCertificateId) {
        List<ObituaryResponseDTO> deathCertificates = adminService.getAllObituariesByCertificateId(deathCertificateId);
        return ResponseEntity.ok(deathCertificates);
    }

    @GetMapping("/messages/{deathCertificateId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllMessagesByDeathCertificate(@PathVariable Long deathCertificateId) {
        List<MessageResponseDTO> deathCertificates = adminService.getAllMessagesByCertificateId(deathCertificateId);
        return ResponseEntity.ok(deathCertificates);
    }


    @PutMapping("/approve/{deathCertificateId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> approveDeathCertificate(@RequestBody @Valid ValidCertificateRequestDTO request,@PathVariable Long deathCertificateId) {
        adminService.verificateDeathCertificate(deathCertificateId, request.getDeathDate());
        return ResponseEntity.ok("Certificate approved successfully");
    }


    @DeleteMapping("/disapprove/{deathCertificateId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> disapproveDeathCertificate(@PathVariable Long deathCertificateId) {
        adminService.disapproveCertificate(deathCertificateId);
        return ResponseEntity.ok("Certificate disapproved successfully");
    }




    




    

}