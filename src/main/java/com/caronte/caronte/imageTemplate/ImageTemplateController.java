package com.caronte.caronte.imageTemplate;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class ImageTemplateController {

    private final ImageTemplateService imageTemplateService;

    public ImageTemplateController(ImageTemplateService imageTemplateService) {
        this.imageTemplateService = imageTemplateService;
    }

    @GetMapping("/urls")
    public List<ImageTemplate> getAllTemplateUrls() {
        return imageTemplateService.getAllTemplateUrls();
    }
}
