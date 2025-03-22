package com.caronte.caronte.video;

import org.springframework.stereotype.Repository;

import com.caronte.caronte.util.LongRepository;

@Repository
public interface VideoRepository extends LongRepository<Video>{
    
}
