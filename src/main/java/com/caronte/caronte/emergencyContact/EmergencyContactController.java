package com.caronte.caronte.emergencyContact;

import com.caronte.caronte.emergencyContact.DTOs.EmergencyContactDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.http.HttpResponse;
import java.util.List;

@Controller
@RequestMapping("/api/contacts")
public class EmergencyContactController {

    private EmergencyContactService emergencyContactService;

    public EmergencyContactController (EmergencyContactService emergencyContactService) {
        this.emergencyContactService = emergencyContactService;
    }

    @GetMapping
    public ResponseEntity<?> getEmergencyContacts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        List<EmergencyContactDTO> emergencyContacts = emergencyContactService.findAll(email);
        return ResponseEntity.ok().body(emergencyContacts);
    }


}
