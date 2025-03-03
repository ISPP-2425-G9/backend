package com.caronte.caronte.imageTemplate;


import org.springframework.stereotype.Service;

@Service
public class ImageTemplateService {

    ImageTemplateRepository imageTemplateRepository;

    public ImageTemplateService(ImageTemplateRepository imageTemplateRepository) {
        this.imageTemplateRepository = imageTemplateRepository;
    }
    
    public ImageTemplate findById(Long id) {
        return imageTemplateRepository.findById(id).get();
    }
}
