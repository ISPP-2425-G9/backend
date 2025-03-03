package com.caronte.caronte.receiver;

import org.springframework.stereotype.Service;

@Service
public class ReceiverService {


    ReceiverRepository receiverRepository;

    public ReceiverService(ReceiverRepository receiverRepository) {
        this.receiverRepository = receiverRepository;
    }
    
}
