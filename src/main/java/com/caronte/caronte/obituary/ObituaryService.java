package com.caronte.caronte.obituary;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.imageTemplate.ImageTemplate;
import com.caronte.caronte.imageTemplate.ImageTemplateService;
import com.caronte.caronte.receiver.ReceiverService;
import com.caronte.caronte.util.MediaHandler;

@Service
public class ObituaryService {

    ObituaryRepository obituaryRepository;
    CustomerRepository customerRepository;
    ImageTemplateService imageTemplateService;
    ReceiverService receiverService;

    public ObituaryService(ObituaryRepository obituaryRepository, CustomerRepository customerRepository,
            ImageTemplateService imageTemplateService, ReceiverService receiverService) {
        this.receiverService = receiverService;
        this.customerRepository = customerRepository;
        this.imageTemplateService = imageTemplateService;
        this.obituaryRepository = obituaryRepository;
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
        Boolean isMine = Boolean.parseBoolean(request.getIsMine());

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        ImageTemplate imageTemplate = imageTemplateService.findById(imageTemplateId);

        String customUrl = request.getCustomImage();
        String customImageUrl = null;

        if (customUrl != null && customUrl.startsWith("data:image/")) {
            customImageUrl = MediaHandler.uploadImageToCloudinary(MediaHandler.base64ToImage(customUrl));
        } else {
            customImageUrl = customUrl;
        }

        Obituary obituary = saveObituary(name, birthDate, deathDate, customImageUrl, farewellMessage, farewellPhrase,
                isMine, customer, imageTemplate);

        List<ObituraryRequestDto.ContactDto> contacts = request.getContacts();
        for (ObituraryRequestDto.ContactDto contact : contacts) {
            receiverService.saveObituaryReceiver(contact.getName(), contact.getPhone(), contact.getEmail(), obituary);
        }

        return obituary;
    }

    @Transactional
    public Obituary saveObituary(String name, LocalDate birth_date, LocalDate death_date, String custom_image_url,
            String farewell_message, String farewell_phrase, Boolean is_mine, Customer customer,
            ImageTemplate imageTemplate) {
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
        return obituaryRepository.save(obituary);
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
        }
        return obituaries;

    }

    @Transactional(readOnly = true)
    public Obituary getObituaryById(Long obituaryId) {
        Obituary obituary = obituaryRepository.findById(obituaryId)
                .orElseThrow(() -> new IllegalArgumentException("Obituary not found"));
        return obituary;

    }

}
