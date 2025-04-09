package com.caronte.caronte.deathCertificate;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caronte.caronte.deathCertificate.DTOs.DeathCertificateRequestDTO;
import com.caronte.caronte.deathCertificate.DTOs.DeathCertificateWithObituaryDniDTO;
import com.caronte.caronte.user.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/deathCertificate")
public class DeathCertificateController {

    private final DeathCertificateService deathCertificateService;
    private final UserService userService;

    public DeathCertificateController(DeathCertificateService deathCertificateService, UserService userService) {
        this.deathCertificateService = deathCertificateService;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDeathCertificate(
            @RequestBody @Valid DeathCertificateRequestDTO deathCertificateRequestDTO) {
        deathCertificateService.createDeathCertificateAndRelations(deathCertificateRequestDTO, null);
        return ResponseEntity.ok("Death Certificate uploaded successfully");
    }

    @PostMapping("/upload/loggedInUser")
    public ResponseEntity<String> uploadDeathCertificateLogged(
            @RequestBody @Valid DeathCertificateRequestDTO deathCertificateRequestDTO, Long customerId) {
        deathCertificateService.createDeathCertificateAndRelations(deathCertificateRequestDTO, customerId);
        return ResponseEntity.ok("Death Certificate uploaded successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<DeathCertificate>> getAllDeathCertificates() {
        return ResponseEntity.ok(deathCertificateService.getAllDeathCertificates());
    }

    @GetMapping("/obituary/{obituaryId}")
    public ResponseEntity<DeathCertificateWithObituaryDniDTO> getDeathCertificateByObituaryId(@PathVariable Long obituaryId) {
        return ResponseEntity.ok(deathCertificateService.getDeathCertificateByObituaryId(obituaryId));
    }

}
