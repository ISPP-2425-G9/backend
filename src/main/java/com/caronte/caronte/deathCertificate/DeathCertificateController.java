package com.caronte.caronte.deathCertificate;

import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;


@RestController
@RequestMapping("api/deathCertificate")
public class DeathCertificateController {

private final DeathCertificateService deathCertificateService;

public DeathCertificateController(DeathCertificateService deathCertificateService) {
    this.deathCertificateService = deathCertificateService;
}

@PostMapping("/upload")
public ResponseEntity<?> uploadDeathCertificate(@RequestBody @Valid DeathCertificateRequestDTO deathCertificateRequestDTO,
        Authentication authentication){ 
    try {

        deathCertificateService.createDeathCertificateAndRelations(deathCertificateRequestDTO);
        return ResponseEntity.ok("Death Certificate uploaded successfully");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }

}

@GetMapping("/all")
public ResponseEntity<?> getAllDeathCertificates() {
    try {
        return ResponseEntity.ok(deathCertificateService.getAllDeathCertificates());
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }
}

@GetMapping("/obituary/{obituaryId}")
public ResponseEntity<?> getDeathCertificateByObituaryId(@PathVariable Long obituaryId) {
    try {
        return ResponseEntity.ok(deathCertificateService.getDeathCertificateByObituaryId(obituaryId));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }
}

@ExceptionHandler({ MethodArgumentNotValidException.class, IllegalArgumentException.class })
public ResponseEntity<Map<String, String>> handleValidationExceptions(Exception ex) {
    Map<String, String> errors = new HashMap<>();

    if (ex instanceof MethodArgumentNotValidException) {
        ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    } else {
        errors.put("error", ex.getMessage());
    }

    return ResponseEntity.badRequest().body(errors);
}



}
