package com.caronte.caronte.message;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.caronte.caronte.util.LongRepository;

@Repository
public interface MessageRepository extends LongRepository<Message> {

    List<Message> findAllByCustomerId(Long customerId);
}
