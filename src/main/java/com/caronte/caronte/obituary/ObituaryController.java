package com.caronte.caronte.obituary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.caronte.caronte.imageTemplate.ImageTemplateService;
import com.caronte.caronte.receiver.ReceiverService;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.imageTemplate.ImageTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Obituary> createObituary(@RequestBody Map<String, Object> request) {
        Obituary obituary = null;
        try{
            String name = (String) request.get("name");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date birthDate = dateFormat.parse((String)request.get("birthDate"));
            Date deathDate = dateFormat.parse((String)request.get("deathDate"));
            String customImageUrl = (String) request.get("customImage");
            String farewellMessage = (String) request.get("farewellMessage");;
            String farewellPhrase = (String) request.get("farewellPhrase");
            Long customerId = Long.parseLong(request.get("customer_id").toString());
            Long imageTemplateId = Long.parseLong(request.get("imageTemplate_id").toString());
            Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
            ImageTemplate imageTemplate = imageTemplateService.findById(imageTemplateId);
            Boolean is_mine = Boolean.parseBoolean(request.get("isMine").toString());

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
            
            List<Map<String, Object>> contacts = (List<Map<String, Object>>) request.get("contacts");
            for (Map<String, Object> contact : contacts) {
                String contactName = (String) contact.get("name");
                String contactTelephone = (String) contact.get("phone");
                String contactEmail = (String) contact.get("email");
                receiverService.saveObituaryReceiver(contactName, contactTelephone, contactEmail, obituary);   
            }  

            
        }catch(Exception e){
            System.out.println(e);
        } 
        return ResponseEntity.ok(obituary);
    }
}




    

