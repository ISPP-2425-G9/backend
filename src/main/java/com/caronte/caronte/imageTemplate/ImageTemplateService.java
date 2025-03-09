package com.caronte.caronte.imageTemplate;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageTemplateService {

    ImageTemplateRepository imageTemplateRepository;

    public ImageTemplateService(ImageTemplateRepository imageTemplateRepository) {
        this.imageTemplateRepository = imageTemplateRepository;
    }
    @Transactional
    public ImageTemplate findById(Long id) {
        ImageTemplate imageTemplate = imageTemplateRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Image template not found"));
        return imageTemplate;
    }
    @Transactional
    public List<ImageTemplate> getAllTemplateUrls() {
        return imageTemplateRepository.findAll()
                .stream()
                .collect(Collectors.toList());
    }
}
