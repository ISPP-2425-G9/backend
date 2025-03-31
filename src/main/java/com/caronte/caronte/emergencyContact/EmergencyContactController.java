package com.caronte.caronte.emergencyContact;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.caronte.caronte.emergencyContact.DTOs.EmergencyContactDTO;
import com.caronte.caronte.user.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/api/contacts")
public class EmergencyContactController {

    private final EmergencyContactService emergencyContactService;
    private final UserService userService;

    public EmergencyContactController (EmergencyContactService emergencyContactService, UserService userService) {
        this.emergencyContactService = emergencyContactService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<EmergencyContactDTO>> getEmergencyContacts() {
        String email = userService.findCurrentUserEmail();
        List<EmergencyContactDTO> emergencyContacts = emergencyContactService.findAll(email);
        return ResponseEntity.ok().body(emergencyContacts);
    }

    @PostMapping
    public ResponseEntity<EmergencyContactDTO> saveEmergencyContact(@RequestBody @Valid EmergencyContactDTO emergencyContactDTO) {
        System.out.println("emergencyContactDTO: " + emergencyContactDTO);
        String email = userService.findCurrentUserEmail();
        EmergencyContactDTO emergencyContact = emergencyContactService.save(emergencyContactDTO, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(emergencyContact);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmergencyContactDTO> updateEmergencyContact(@RequestBody @Valid EmergencyContactDTO emergencyContactDTO,
                                                    @PathVariable Long id) {
        String email = userService.findCurrentUserEmail();
        EmergencyContactDTO emergencyContact = emergencyContactService.update(emergencyContactDTO, id, email);
        return ResponseEntity.ok().body(emergencyContact);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmergencyContact(@PathVariable Long id) {
        String email = userService.findCurrentUserEmail();
        emergencyContactService.delete(id, email);
        return ResponseEntity.noContent().build();
    }


}
