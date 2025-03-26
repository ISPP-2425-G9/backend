package com.caronte.caronte.image;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.caronte.caronte.util.LongRepository;

@Repository
public interface ImageRepository extends LongRepository<Image> {

    @Query("SELECT i FROM Image i WHERE i.message.id = ?1")
    List<Image> findAllByMessageId(Long message_id);
    
}
