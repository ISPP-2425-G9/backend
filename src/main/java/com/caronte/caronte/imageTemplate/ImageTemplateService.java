package com.caronte.caronte.imageTemplate;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.util.exceptions.ResourceNotFound;

@Service
public class ImageTemplateService {

    ImageTemplateRepository imageTemplateRepository;

    public ImageTemplateService(ImageTemplateRepository imageTemplateRepository) {
        this.imageTemplateRepository = imageTemplateRepository;
    }

    @Transactional
    public ImageTemplate findById(Long id) {
        return imageTemplateRepository.findById(id).orElseThrow(() -> ResourceNotFound.of("Image template"));
    }

    @Transactional
    public List<ImageTemplate> getAllTemplateUrls() {
        return imageTemplateRepository.findAll();
    }
}
