package com.caronte.caronte.receiver;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituraryRequestDto.ContactDto;

@Service
public class ReceiverService {

    private final ReceiverRepository receiverRepository;

    public ReceiverService(ReceiverRepository receiverRepository) {
        this.receiverRepository = receiverRepository;
    }

    @Transactional(readOnly = true)
    public List<ReceiverResponseDTO> getReceiversByObituaryId(Obituary obituary) {
        List<Receiver> receiversList = receiverRepository.findByObituary(obituary);
        List<ReceiverResponseDTO> receivers = receiversList.stream().map(ReceiverResponseDTO::parse).toList();
        return receivers;
    }

    @Transactional
    public Receiver saveObituaryReceiver(ContactDto contactDto, Obituary obituary) {
        Receiver receiver = Receiver.parse(contactDto, obituary);
        return receiverRepository.save(receiver);
    }

    @Transactional
    public List<Receiver> saveAllObituaryReceiver(List<ContactDto> contactsDto, Obituary obituary) {
        List<Receiver> receivers = contactsDto.stream().map(contactDto -> Receiver.parse(contactDto, obituary)).toList();
        return receiverRepository.saveAll(receivers);
    }

    @Transactional
    public void deleteReceiversByObituaryId(Obituary obituary) {
        receiverRepository.deleteByObituary(obituary);
        receiverRepository.flush();
    }
        
}
