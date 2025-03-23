package com.caronte.caronte.receiver;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.message.Message;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituraryRequestDto.ContactDto;

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
    public List<Receiver> saveAllObituaryReceiver(List<ContactDto> contactsDto, Obituary obituary) {
        List<Receiver> receivers = contactsDto.stream().map(contactDto -> {
            Receiver receiver = new Receiver();
            receiver.setName(contactDto.getName());
            receiver.setTelephone(contactDto.getPhone());
            receiver.setEmail(contactDto.getEmail());
            receiver.setObituary(obituary);
            return receiver;
        }).toList();

        return receiverRepository.saveAll(receivers);
    }

    @Transactional
    public void deleteReceiversByObituaryId(Obituary obituary) {
        receiverRepository.deleteByObituary(obituary);
        receiverRepository.flush();
    }
    
    @Transactional(readOnly = true)
    public List<ReceiverResponseDTO> getReceiversByObituaryId(Obituary obituary) {
        List<Receiver> receiversList = receiverRepository.findByObituary(obituary);
        List<ReceiverResponseDTO> receivers = receiversList.stream().map(receiver -> {
            ReceiverResponseDTO receiverResponseDTO = new ReceiverResponseDTO();
            receiverResponseDTO.setId(receiver.getId());
            receiverResponseDTO.setName(receiver.getName());
            receiverResponseDTO.setTelephone(receiver.getTelephone());
            receiverResponseDTO.setEmail(receiver.getEmail());
            return receiverResponseDTO;
        }).toList();
        return receivers;
    }

    @Transactional
    public Receiver saveMessageReceiver(String name, String telephone, String email, Message message) {
        Receiver receiver = new Receiver();
        receiver.setName(name);
        receiver.setTelephone(telephone);
        receiver.setEmail(email);
        receiver.setMessage(message);
        return receiverRepository.save(receiver);
    }
    
    
}
