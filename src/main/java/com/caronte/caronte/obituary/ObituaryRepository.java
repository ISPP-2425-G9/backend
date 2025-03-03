package com.caronte.caronte.obituary;

import org.springframework.stereotype.Repository;

import com.caronte.caronte.util.LongRepository;

@Repository
public interface ObituaryRepository extends LongRepository<Obituary> {
    
}
