package com.caronte.caronte.receiver;

import java.util.List;

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

    public void deleteReceiversByObituaryId(Obituary obituary) {
        receiverRepository.deleteByObituary(obituary);
    }

    public List<Receiver> getReceiversByObituaryId(Obituary obituary) {
        return receiverRepository.findByObituary(obituary);
    }

    
}
