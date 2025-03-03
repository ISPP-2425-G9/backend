package com.caronte.caronte.obituary;

import org.springframework.stereotype.Service;

@Service
public class ObituaryService {
    
    ObituaryRepository obituaryRepository;

    public ObituaryService(ObituaryRepository obituaryRepository) {
        this.obituaryRepository = obituaryRepository;
    }

}
