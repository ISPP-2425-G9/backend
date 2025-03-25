package com.caronte.caronte.emergencyContact;

import com.caronte.caronte.emergencyContact.DTOs.EmergencyContactDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<?> saveEmergencyContact(@RequestBody @Valid EmergencyContactDTO emergencyContactDTO) {
        System.out.println("emergencyContactDTO: " + emergencyContactDTO);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        EmergencyContactDTO emergencyContact = emergencyContactService.save(emergencyContactDTO, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(emergencyContact);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmergencyContact(@RequestBody @Valid EmergencyContactDTO emergencyContactDTO,
                                                    @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        EmergencyContactDTO emergencyContact = emergencyContactService.update(emergencyContactDTO, id, email);
        return ResponseEntity.ok().body(emergencyContact);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmergencyContact(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        emergencyContactService.delete(id, email);
        return ResponseEntity.noContent().build();
    }


}
