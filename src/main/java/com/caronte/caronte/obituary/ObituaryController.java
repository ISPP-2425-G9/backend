package com.caronte.caronte.obituary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.caronte.caronte.imageTemplate.ImageTemplateService;
import com.caronte.caronte.obituary.ObituraryRequestDto.ContactDto;
import com.caronte.caronte.receiver.ReceiverService;

import jakarta.validation.Valid;

import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.imageTemplate.ImageTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("api/obituary")
public class ObituaryController {

    private final ObituaryService obituaryService;
    private final CustomerRepository customerRepository;
    private final ImageTemplateService imageTemplateService;
    private final ReceiverService receiverService;

    public ObituaryController(ObituaryService obituaryService, CustomerRepository customerRepository,ImageTemplateService imageTemplateService, ReceiverService receiverService) {
        this.obituaryService = obituaryService;
        this.customerRepository = customerRepository;
        this.imageTemplateService = imageTemplateService;
        this.receiverService = receiverService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createObituary(@RequestBody @Valid ObituraryRequestDto request, Authentication authentication) {
        Obituary obituary = null;
        try{
            
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            String name = request.getName();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate birthDate = null;
            LocalDate deathDate = null;
            if( request.getDeathDate() != ""){
                deathDate = LocalDate.parse(request.getDeathDate(), formatter);
                birthDate = LocalDate.parse(request.getBirthDate(), formatter);
            }
            String customImageUrl = request.getCustomImage();
            String farewellMessage = request.getFarewellMessage();
            String farewellPhrase = request.getFarewellPhrase();
            Long imageTemplateId = Long.parseLong(request.getImageTemplate_id().toString());
            Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
            ImageTemplate imageTemplate = imageTemplateService.findById(imageTemplateId);
            Boolean is_mine = request.getIsMine();

            obituary = obituaryService.saveObituary(
                 name,
                 birthDate,
                 deathDate,
                 customImageUrl,
                 farewellMessage,
                 farewellPhrase,
                 is_mine,
                 customer,
                 imageTemplate
             );
            
             List<ContactDto> contacts = request.getContacts();
             for (ContactDto contact : contacts) {
                 String contactName = contact.getName();
                 String contactTelephone = contact.getPhone();
                 String contactEmail = contact.getEmail();
                 receiverService.saveObituaryReceiver(contactName, contactTelephone, contactEmail, obituary);   
             }  

            
        }catch(Exception e){
            System.out.println(e);
        } 
        return ResponseEntity.ok("Obituary created successfully");
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




    

