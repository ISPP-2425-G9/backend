package com.caronte.caronte.healthStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/status")
public class HealthStatusController {

    @GetMapping
    public ResponseEntity<Boolean> getHealthStatus() {
        return ResponseEntity.ok(true);
    }

}