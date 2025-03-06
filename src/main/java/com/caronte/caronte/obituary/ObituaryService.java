package com.caronte.caronte.obituary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObituaryService {

    private ObituaryRepository obituaryRepository;

    @Autowired
    public ObituaryService(ObituaryRepository obituaryRepository) {
        this.obituaryRepository = obituaryRepository;
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
