package com.caronte.caronte.obituary;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.imageTemplate.ImageTemplate;

@Service
public class ObituaryService {
    
    ObituaryRepository obituaryRepository;

    public ObituaryService(ObituaryRepository obituaryRepository) {
        this.obituaryRepository = obituaryRepository;
    }

    public Obituary saveObituary(String name, LocalDate birth_date, LocalDate death_date, String custom_image_url, String farewell_message, String farewell_phrase, Boolean is_mine, Customer customer, ImageTemplate imageTemplate) {
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


    public Obituary findById(Long id) {
        return obituaryRepository.findById(id).orElseThrow(() -> new RuntimeException("Obituary not found"));
    }

    public Obituary updateObituary(Obituary obituary) {
        return obituaryRepository.save(obituary);
    }

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
