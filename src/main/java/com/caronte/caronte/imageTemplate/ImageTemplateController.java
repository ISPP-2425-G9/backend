package com.caronte.caronte.imageTemplate;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/templates")
public class ImageTemplateController {

    private final ImageTemplateService imageTemplateService;

    public ImageTemplateController(ImageTemplateService imageTemplateService) {
        this.imageTemplateService = imageTemplateService;
    }

    @GetMapping("/urls")
    public ResponseEntity<List<ImageTemplate>> getAllTemplateUrls() {
        return ResponseEntity.ok(imageTemplateService.getAllTemplateUrls());
    }
}
