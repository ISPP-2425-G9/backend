package com.caronte.caronte.obituary;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.caronte.caronte.obituary.DTOs.ObituraryRequestDto;
import com.caronte.caronte.user.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/obituary")
public class ObituaryController {

    private final ObituaryService obituaryService;
    private final UserService userService;

    public ObituaryController(ObituaryService obituaryService,  UserService userService) {
        this.obituaryService = obituaryService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createObituary(@RequestBody @Valid ObituraryRequestDto request) {
        Long customerId = userService.findCurrentUserId();
        obituaryService.createObituaryWithReceivers(request, customerId);
        return ResponseEntity.ok("Obituary created successfully");
    }

    @PutMapping("/update/{obituaryId}")
    public ResponseEntity<String> updateObituary(@RequestBody @Valid ObituraryRequestDto request,
            @PathVariable Long obituaryId) {
        Long customerId = userService.findCurrentUserId();
        obituaryService.updateObituaryWithReceivers(customerId, obituaryId, request);
        return ResponseEntity.ok("Obituary updated successfully");
    }

    @DeleteMapping("/delete/{obituaryId}")
    public ResponseEntity<String> deleteObituary(@PathVariable Long obituaryId, Authentication authentication) {
        Long customerId = userService.findCurrentUserId();
        obituaryService.deleteObituaryByCustomer(customerId, obituaryId);
        return ResponseEntity.ok("Obituary deleted successfully");
    }

    @GetMapping("/myObituaries")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Obituary>> getAllObituariesByCustomer() {
        Long customerId = userService.findCurrentUserId();
        List<Obituary> obituaries = obituaryService.getAllObituariesByCustomer(customerId);
        return ResponseEntity.ok().body(obituaries);
    }

    @GetMapping("/myObituaries/{obituaryId}")
    public ResponseEntity<Obituary> getObituaryById(@PathVariable Long obituaryId) {
        Long userId = userService.findCurrentUserId();
        Obituary obituary = obituaryService.getObituaryById(obituaryId, userId);
        obituary.setCustomer(null);
        return ResponseEntity.ok().body(obituary);
    }

}
