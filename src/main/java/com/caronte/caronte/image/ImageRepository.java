package com.caronte.caronte.image;

import org.springframework.stereotype.Repository;

import com.caronte.caronte.util.LongRepository;

@Repository
public interface ImageRepository extends LongRepository<Image> {
    
}
