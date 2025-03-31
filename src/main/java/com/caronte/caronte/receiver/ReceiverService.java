package com.caronte.caronte.receiver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.configuration.services.EmailService;
import com.caronte.caronte.message.Message;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituraryRequestDto.ContactDto;

@Service
public class ReceiverService {

    ReceiverRepository receiverRepository;
    EmailService emailService;

    public ReceiverService(ReceiverRepository receiverRepository, EmailService emailService) {
        this.emailService = emailService;
        this.receiverRepository = receiverRepository;
    }

    @Transactional
    public Receiver saveObituaryReceiver(String name, String telephone, String email, Obituary obituary) {
        Receiver receiver = new Receiver();
        receiver.setName(name);
        receiver.setTelephone(telephone);
        receiver.setEmail(email);
        receiver.setObituary(obituary);
        receiver = receiverRepository.saveAndFlush(receiver);
        return receiver;
    }

    @Transactional
    public void deleteReceiversByObituaryId(Obituary obituary) {
        receiverRepository.deleteByObituary(obituary);
        receiverRepository.flush();
    }

    @Transactional(readOnly = true)
    public List<ReceiverResponseDTO> getReceiversByObituaryId(Obituary obituary) {
        List<ReceiverResponseDTO> receivers = new ArrayList<>();
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

    @Transactional
    public Receiver saveMessageReceiver(String name, String telephone, String email, Message message) {
        Receiver receiver = new Receiver();
        receiver.setName(name);
        receiver.setTelephone(telephone);
        receiver.setEmail(email);
        receiver.setMessage(message);
        return receiverRepository.save(receiver);
    }

    @Transactional
    public Receiver updateMessageReceiver(Long id, String name, String telephone, String email) {
        Receiver receiver = receiverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        if (!receiver.getName().equals(name)) {
            receiver.setName(name);
        }
        if (!receiver.getTelephone().equals(telephone)) {
            receiver.setTelephone(telephone);
        }
        if (!receiver.getEmail().equals(email)) {
            receiver.setEmail(email);
        }
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
