package com.caronte.caronte.receiver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.obituary.Obituary;

@Service
public class ReceiverService {


    ReceiverRepository receiverRepository;

    public ReceiverService(ReceiverRepository receiverRepository) {
        this.receiverRepository = receiverRepository;
    }
    @Transactional
    public Receiver saveObituaryReceiver(String name, String telephone, String email, Obituary obituary) {
        Receiver receiver = new Receiver();
        receiver.setName(name);
        receiver.setTelephone(telephone);
        receiver.setEmail(email);
        receiver.setObituary(obituary);
        return receiverRepository.save(receiver);
    }
    @Transactional
    public void deleteReceiversByObituaryId(Obituary obituary) {
        receiverRepository.deleteByObituary(obituary);
        receiverRepository.flush();
    }
    @Transactional(readOnly = true)
    public List<ReceiverResponseDTO> getReceiversByObituaryId(Obituary obituary) {
        List<ReceiverResponseDTO> receivers = new ArrayList();
        List<Receiver> receiversList = receiverRepository.findByObituary(obituary);
        for (Receiver receiver : receiversList) {
            ReceiverResponseDTO receiverResponseDTO = new ReceiverResponseDTO();
            receiverResponseDTO.setId(receiver.getId());
            receiverResponseDTO.setName(receiver.getName());
            receiverResponseDTO.setTelephone(receiver.getTelephone());
            receiverResponseDTO.setEmail(receiver.getEmail());
            receivers.add(receiverResponseDTO);
        }

        return receivers;
    }



    
}
