package com.caronte.caronte.obituary;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.imageTemplate.ImageTemplate;
import com.caronte.caronte.imageTemplate.ImageTemplateService;
import com.caronte.caronte.receiver.Receiver;
import com.caronte.caronte.receiver.ReceiverService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/obituary")
public class ObituaryController {

    private final ObituaryService obituaryService;
    private final CustomerRepository customerRepository;
    private final ImageTemplateService imageTemplateService;
    private final ReceiverService receiverService;

    public ObituaryController(ObituaryService obituaryService, CustomerRepository customerRepository,
            ImageTemplateService imageTemplateService, ReceiverService receiverService) {
        this.obituaryService = obituaryService;
        this.customerRepository = customerRepository;
        this.imageTemplateService = imageTemplateService;
        this.receiverService = receiverService;
    }

    private LocalDate parseDate(String input) {
        if (input == null || input.trim().isEmpty())
            return null;

        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(input, formatter);
            } catch (Exception ignored) {
            }
        }

        throw new RuntimeException("Formato de fecha inválido: " + input);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createObituary(@RequestBody @Valid ObituraryRequestDto request,
            Authentication authentication) {
        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();

            Obituary obituary = obituaryService.createObituaryWithReceivers(request, customerId);

            System.out.println(obituary);

            return ResponseEntity.ok("Obituary created successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/update/{obituary_id}")
    public ResponseEntity<?> updateObituary(@RequestBody @Valid ObituraryRequestDto request,
            @PathVariable Long obituary_id, Authentication authentication) {
        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            Obituary oldObituary = obituaryService.findById(obituary_id);

            if (oldObituary.getCustomer().getId() != customerId) {
                return ResponseEntity.badRequest().body(Map.of("error", "You are not allowed to update this obituary"));
            }

            String name = request.getName();
            LocalDate birthDate = parseDate(request.getBirthDate());
            LocalDate deathDate = parseDate(request.getDeathDate());

            String customImageUrl = request.getCustomImage();
            String farewellMessage = request.getFarewellMessage();
            String farewellPhrase = request.getFarewellPhrase();
            Long imageTemplateId = request.getImageTemplate_id();
            ImageTemplate imageTemplate = imageTemplateService.findById(imageTemplateId);
            Boolean isMine = Boolean.parseBoolean(request.getIsMine());

            oldObituary.setName(name);
            oldObituary.setBirthDate(birthDate);
            oldObituary.setDeathDate(deathDate);
            oldObituary.setCustomImageUrl(customImageUrl);
            oldObituary.setFarewellMessage(farewellMessage);
            oldObituary.setFarewellPhrase(farewellPhrase);
            oldObituary.setIsMine(isMine);
            oldObituary.setImageTemplate(imageTemplate);

            Obituary newObituary = obituaryService.updateObituary(oldObituary);

            receiverService.deleteReceiversByObituaryId(newObituary);

            List<ObituraryRequestDto.ContactDto> contacts = request.getContacts();
            for (ObituraryRequestDto.ContactDto contact : contacts) {
                receiverService.saveObituaryReceiver(contact.getName(), contact.getPhone(), contact.getEmail(),
                        newObituary);
            }

            return ResponseEntity.ok("Obituary updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{obituary_id}")
    public ResponseEntity<String> deleteObituary(@PathVariable Long obituary_id, Authentication authentication) {
        Obituary obituary = null;
        try {

            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();

            obituary = obituaryService.findById(obituary_id);

            if (obituary.getCustomer().getId() != customerId) {
                return ResponseEntity.badRequest().body("You are not allowed to delete this obituary");
            }
            obituaryService.deleteObituary(obituary_id);

        } catch (Exception e) {
            System.out.println(e);
        }
        return ResponseEntity.ok("Obituary deleted successfully");
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
            if (obituary.getCustomer().getId() != customerId) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't access this data");
            }
            obituary.setCustomer(null);
            return ResponseEntity.ok().body(obituary);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/receivers/{obituaryId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getReceiversByObituaryId(@PathVariable Long obituaryId, Authentication authentication) {
        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            Obituary obituary = obituaryService.getObituaryById(obituaryId);
            List<Receiver> receivers = receiverService.getReceiversByObituaryId(obituary);
            if (obituary.getCustomer().getId() != customerId) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't access this data");
            }
            obituary.setCustomer(null);
            return ResponseEntity.ok().body(receivers);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
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
