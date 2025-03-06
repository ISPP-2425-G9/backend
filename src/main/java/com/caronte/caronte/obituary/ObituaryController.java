package com.caronte.caronte.obituary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.configuration.services.UserDetailsImpl;

@RestController
@RequestMapping("api/obituary")
public class ObituaryController {

    private ObituaryService obituaryService;

    public ObituaryController(ObituaryService obituaryService) {
        this.obituaryService = obituaryService;
    }

    @GetMapping("/myObituaries")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllObituariesByCustomer(Authentication authentication) {
        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            Iterable<Obituary> obituaries = obituaryService.getAllObituariesByCustomer(customerId);
            return ResponseEntity.ok().body(obituaries);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/myObituaries/{obituaryId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getObituaryById(@PathVariable Long obituaryId, Authentication authentication) {
        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            Obituary obituary = obituaryService.getObituaryById(obituaryId);
            System.out.println(obituary.getCustomer().getId());
            if (obituary.getCustomer().getId() != customerId) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't access this data");
            }
            obituary.setCustomer(null);
            return ResponseEntity.ok().body(obituary);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
