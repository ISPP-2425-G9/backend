package com.caronte.caronte.receiver;

import org.springframework.stereotype.Service;

import com.caronte.caronte.obituary.Obituary;

@Service
public class ReceiverService {


    ReceiverRepository receiverRepository;

    public ReceiverService(ReceiverRepository receiverRepository) {
        this.receiverRepository = receiverRepository;
    }

    public Receiver saveObituaryReceiver(String name, String telephone, String email, Obituary obituary) {
        Receiver receiver = new Receiver();
        receiver.setName(name);
        receiver.setTelephone(telephone);
        receiver.setEmail(email);
        receiver.setObituary(obituary);
        return receiverRepository.save(receiver);
    }

    
}
