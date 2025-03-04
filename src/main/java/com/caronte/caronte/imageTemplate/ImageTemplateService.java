package com.caronte.caronte.imageTemplate;


import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageTemplateService {

    ImageTemplateRepository imageTemplateRepository;

    public ImageTemplateService(ImageTemplateRepository imageTemplateRepository) {
        this.imageTemplateRepository = imageTemplateRepository;
    }
    
    public ImageTemplate findById(Long id) {
        return imageTemplateRepository.findById(id).get();
    }

    public List<ImageTemplate> getAllTemplateUrls() {
        return imageTemplateRepository.findAll()
                .stream()
                .collect(Collectors.toList());
    }
}
