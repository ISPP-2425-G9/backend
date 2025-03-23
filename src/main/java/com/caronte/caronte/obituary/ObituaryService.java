package com.caronte.caronte.obituary;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.deathCertificate.DeathCertificateService;
import com.caronte.caronte.imageTemplate.ImageTemplate;
import com.caronte.caronte.imageTemplate.ImageTemplateService;
import com.caronte.caronte.receiver.ReceiverService;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.util.exceptions.ResponseThrow;

@Service
public class ObituaryService {

    ObituaryRepository obituaryRepository;
    CustomerRepository customerRepository;
    ImageTemplateService imageTemplateService;
    ReceiverService receiverService;
    DeathCertificateService deathCertificateService;

    public ObituaryService(ObituaryRepository obituaryRepository, CustomerRepository customerRepository,
            ImageTemplateService imageTemplateService, ReceiverService receiverService, DeathCertificateService deathCertificateService) {
        this.receiverService = receiverService;
        this.customerRepository = customerRepository;
        this.imageTemplateService = imageTemplateService;
        this.obituaryRepository = obituaryRepository;
        this.deathCertificateService = deathCertificateService;
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

    @Transactional
    public Obituary createObituaryWithReceivers(ObituraryRequestDto request, Long customerId) {
        String name = request.getName();
        LocalDate birthDate = parseDate(request.getBirthDate());
        LocalDate deathDate = parseDate(request.getDeathDate());

        String farewellMessage = request.getFarewellMessage();
        String farewellPhrase = request.getFarewellPhrase();
        Long imageTemplateId = request.getImageTemplate_id();
        Boolean isMine = request.getIsMine();
        String wordColor = request.getWordColor();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ImageTemplate imageTemplate = imageTemplateService.findById(imageTemplateId);
        DeathCertificate deathCertificate = null;
        
        if(!isMine){
            if(request.getDeathCertificate() == null || request.getDeathCertificate().getDni() == null || request.getDeathCertificate().getFile() == null){
                throw new IllegalArgumentException("The Death Certificate is invalid");
            }
            if (customer.getDni().equals(request.getDeathCertificate().getDni())){
                throw new IllegalArgumentException("No puedes subir un certificado con tu DNI");
            }
            Iterable<Obituary> obituaries = obituaryRepository.findByCustomerDni(request.getDeathCertificate().getDni());
            Boolean existCustomer = customerRepository.existsByDni(request.getDeathCertificate().getDni());

            if(existCustomer && obituaries.iterator().hasNext() && obituaries.iterator().next().getDeathCertificate() == null){
                deathCertificate = deathCertificateService.createDeathCertificateAndRelations(request.getDeathCertificate(), customerId );

            }else{
                deathCertificate = deathCertificateService.createDeathCertificate(request.getDeathCertificate());  
            }
        }
        String customUrl = request.getCustomImage();
        String customImageUrl = null;

        if (customUrl != null && customUrl.startsWith("data:image/")) {
            customImageUrl = MediaHandler.uploadImageToCloudinary(MediaHandler.base64ToImage(customUrl), "obituaries");
        } else {
            customImageUrl = customUrl;
        }

        

        Obituary obituary = saveObituary(name, birthDate, deathDate, customImageUrl, farewellMessage, farewellPhrase,
                isMine, customer, imageTemplate,deathCertificate,wordColor);

        List<ObituraryRequestDto.ContactDto> contacts = request.getContacts();
        for (ObituraryRequestDto.ContactDto contact : contacts) {
            receiverService.saveObituaryReceiver(contact.getName(), contact.getPhone(), contact.getEmail(), obituary);
        }

        return obituary;
    }

    @Transactional
    public Obituary updateObituaryWithReceivers(Long customerId, Long obituaryId, ObituraryRequestDto request) {
        Obituary obituary = findById(obituaryId);

        if (!obituary.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("You are not allowed to update this obituary");
        }

        Boolean isMine = request.getIsMine();
        if(obituary.getIsMine() && !isMine || !obituary.getIsMine() && isMine){
            throw new IllegalArgumentException("You can't change IsMine property");
        }

        if(!isMine && obituary.getDeathCertificate().getIsVerified()){
            throw new IllegalArgumentException("You can't upload the obituary since the death certificate is verified");
        } 

        String name = request.getName();
        LocalDate birthDate = parseDate(request.getBirthDate());
        LocalDate deathDate = parseDate(request.getDeathDate());
        String farewellMessage = request.getFarewellMessage();
        String farewellPhrase = request.getFarewellPhrase();
        Long imageTemplateId = request.getImageTemplate_id();
        
        String customUrl = request.getCustomImage();
        ImageTemplate imageTemplate = imageTemplateService.findById(imageTemplateId);
        String wordColor = request.getWordColor();

        String customImageUrl;
        if (customUrl != null && customUrl.startsWith("data:image/")) {
            customImageUrl = MediaHandler.uploadImageToCloudinary(MediaHandler.base64ToImage(customUrl), "obituaries");
        } else {
            customImageUrl = customUrl;
        }

        Obituary updatedObituary = null;

        obituary.setName(name);
        obituary.setBirthDate(birthDate);
        obituary.setDeathDate(deathDate);
        obituary.setCustomImageUrl(customImageUrl);
        obituary.setFarewellMessage(farewellMessage);
        obituary.setFarewellPhrase(farewellPhrase);
        obituary.setIsMine(isMine);
        obituary.setImageTemplate(imageTemplate);
        obituary.setWordColor(wordColor);

        updatedObituary = updateObituary(obituary);

        receiverService.deleteReceiversByObituaryId(updatedObituary);

        List<ObituraryRequestDto.ContactDto> contacts = request.getContacts();
        for (ObituraryRequestDto.ContactDto contact : contacts) {
            receiverService.saveObituaryReceiver(contact.getName(), contact.getPhone(), contact.getEmail(),
                    updatedObituary);
        }
        
        return updatedObituary;
    }

    @Transactional
    public Obituary saveObituary(String name, LocalDate birth_date, LocalDate death_date, String custom_image_url,
            String farewell_message, String farewell_phrase, Boolean is_mine, Customer customer,
            ImageTemplate imageTemplate, DeathCertificate certificate, String word_color) {
        Obituary obituary = new Obituary();

        obituary.setName(name);
        obituary.setBirthDate(birth_date);
        obituary.setDeathDate(death_date);
        obituary.setCustomImageUrl(custom_image_url);
        obituary.setFarewellMessage(farewell_message);
        obituary.setFarewellPhrase(farewell_phrase);
        obituary.setIsMine(is_mine);
        obituary.setCustomer(customer);
        obituary.setImageTemplate(imageTemplate);
        obituary.setDeathCertificate(certificate);
        obituary.setWordColor(word_color);
        return obituaryRepository.save(obituary);
    }

    @Transactional
    public void deleteObituaryByCustomer(Long customerId, Long obituaryId) {
        Obituary obituary = findById(obituaryId);

        if (!obituary.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("You are not allowed to delete this obituary");
        }

        obituaryRepository.deleteById(obituaryId);
    }

    @Transactional(readOnly = true)
    public Obituary findById(Long id) {
        return obituaryRepository.findById(id).orElseThrow(() -> new RuntimeException("Obituary not found"));
    }

    @Transactional
    public Obituary updateObituary(Obituary obituary) {
        return obituaryRepository.save(obituary);
    }

    @Transactional
    public void deleteObituary(Long id) {
        obituaryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Iterable<Obituary> getAllObituariesByCustomer(Long customerId) {
        Iterable<Obituary> obituaries = obituaryRepository.findByCustomerId(customerId);
        for (Obituary obituary : obituaries) {
            obituary.setCustomer(null);
            if(obituary.getWordColor() == null){
                obituary.setWordColor("0,0,0");
            }
        }
        return obituaries;

    }

    @Transactional(readOnly = true)
    public Obituary getObituaryById(Long obituaryId, Long userId) {
        Obituary obituary = obituaryRepository.findById(obituaryId)
                .orElseThrow(() -> new IllegalArgumentException("Obituary not found"));
        ResponseThrow.checkOrForbidden(!obituary.getCustomer().getId().equals(userId), "You can't access this data");
        if(obituary.getWordColor() == null){
            obituary.setWordColor("0,0,0");
        }
        return obituary;

    }

    @Transactional(readOnly = true)
    public Iterable<Obituary> findObituaryByCustomerDni(String dni) {
        return obituaryRepository.findByCustomerDni(dni);
    }

}
