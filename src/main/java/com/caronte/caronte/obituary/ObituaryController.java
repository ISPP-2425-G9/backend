package com.caronte.caronte.obituary;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.user.UserService;

@RestController
@RequestMapping("api/obituary")
public class ObituaryController {

    private ObituaryService obituaryService;
    private UserService userService;

    public ObituaryController(ObituaryService obituaryService, UserService userService) {
        this.obituaryService = obituaryService;
        this.userService = userService;
    }

    @GetMapping("/{customerId}/my_obituaries")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllObituariesByCustomer(@PathVariable Long customerId) {
        if (userService.findCurrentUser().getId() != customerId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't access this data");
		}
        try {
            Iterable<Obituary> obituaries = obituaryService.getAllObituariesByCustomer(customerId);
            return ResponseEntity.ok().body(obituaries);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/{customerId}/my_obituaries/{obituaryId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getObituaryById(@PathVariable Long customerId, @PathVariable Long obituaryId) {
        try {
            Obituary obituary = obituaryService.getObituaryById(obituaryId);
            if (obituary.getCustomer().getId() != customerId) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't access this data");
            }
            return ResponseEntity.ok().body(obituary);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
