package com.caronte.caronte.receiver;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.configuration.services.EmailService;
import com.caronte.caronte.message.Message;
import com.caronte.caronte.message.DTOs.MessageRequestDto.RecipientDto;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.DTOs.ObituraryRequestDto.ContactDto;
import com.caronte.caronte.receiver.DTOs.ReceiverResponseDTO;
import com.caronte.caronte.util.exceptions.ResourceNotFound;

@Service
public class ReceiverService {

    private final ReceiverRepository receiverRepository;
    private final EmailService emailService;

    public ReceiverService(ReceiverRepository receiverRepository, EmailService emailService) {
        this.emailService = emailService;
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
    public Receiver saveMessageReceiver(RecipientDto recipientDto, Message message) {
        Receiver receiver = Receiver.parse(recipientDto, message);
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
        
    @Transactional
    public Receiver updateMessageReceiver(Long id, RecipientDto recipientDto) {
        Receiver receiver = receiverRepository.findById(id).orElseThrow(() -> ResourceNotFound.of("Receiver"));
        receiver.setName(recipientDto.getName());
        receiver.setTelephone(recipientDto.getTelephone());
        receiver.setEmail(recipientDto.getEmail());
        return receiverRepository.save(receiver);
    }

    @Transactional
    public void notifyReceivers(List<ContactDto> contacts, Obituary obituary) {
        for (ContactDto contact : contacts) {
            try {
                byte[] pdfBytes = emailService.generateObituaryPdf(obituary);

                emailService.sendEmailWithAttachment(contact.getEmail(), "Esquela de " + obituary.getName(),
                        "Adjunto encontrarás la esquela de " + obituary.getName(),
                        pdfBytes, "esquela_" + obituary.getName() + ".pdf");

                System.out.println("Email enviado a: " + contact.getEmail());
            } catch (Exception e) {
                System.out.println("Error al notificar por email a: " + contact.getEmail() + " - " + e.getMessage());
            }
        }
    }

    

}
