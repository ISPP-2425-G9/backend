package com.caronte.caronte.receiver;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.configuration.services.EmailService;
import com.caronte.caronte.message.Message;
import com.caronte.caronte.message.DTOs.MessageRequestDto.RecipientDto;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.DTOs.ObituraryRequestDto.ContactDto;
import com.caronte.caronte.receiver.DTOs.ReceiverResponseDTO;
import com.caronte.caronte.util.AESCipher;
import com.caronte.caronte.util.exceptions.ResourceNotFound;

@Service
public class ReceiverService {

    @Value("${app.domain}")
    private String domain;

    private final ReceiverRepository receiverRepository;
    private final EmailService emailService;
    private final AESCipher aesCipher;
    public ReceiverService(ReceiverRepository receiverRepository, EmailService emailService, AESCipher aesCipher) {
        this.emailService = emailService;
        this.receiverRepository = receiverRepository;
        this.aesCipher = aesCipher;
        
    }
  
    @Transactional(readOnly = true)
    public List<ReceiverResponseDTO> getReceiversByObituaryId(Obituary obituary) {
        List<Receiver> receiversList = receiverRepository.findByObituary(obituary);
        List<ReceiverResponseDTO> receivers = receiversList.stream().map(ReceiverResponseDTO::parse).toList();
        return receivers;
    }

    @Transactional(readOnly = true)
    public List<Receiver> getReceiversByObituary(Obituary obituary) {
        List<Receiver> receiversList = receiverRepository.findByObituary(obituary);
        return receiversList;
    }

    @Transactional
    public Receiver saveObituaryReceiver(ContactDto contactDto, Obituary obituary) {
        Receiver receiver = Receiver.parse(contactDto, obituary);
        return receiverRepository.save(receiver);
    }

    @Transactional
    public Receiver saveReceiverByRecipientDto(RecipientDto recipientDto, Message message) {
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
    public void sendObituary(List<Receiver> receivers, Obituary obituary) {
        for (Receiver receiver : receivers) {
            try {
                byte[] pdfBytes = emailService.generateObituaryPdf(obituary);

                emailService.sendEmailWithAttachment(receiver.getEmail(), "Esquela de " + obituary.getName(),
                        "Adjunto encontrarás la esquela de " + obituary.getName(),
                        pdfBytes, "esquela_" + obituary.getName() + ".pdf");

                System.out.println("Email enviado a: " + receiver.getEmail());
            } catch (Exception e) {
                System.out.println("Error al notificar por email a: " + receiver.getEmail() + " - " + e.getMessage());
            }
        }
    }

    @Transactional
    public void sendMessage(List<Receiver> receivers , Message message) {
        String code = aesCipher.decrypt(message.getCode());
        String messageBody = "Has recibido un mensaje de Caronte. \n" + 
                "El codigo para acceder al mensaje es: " + code + "\n" +
                "Puedes revisarlo aquí: " + domain + "/messages?messageId=" + message.getId();      
        for (Receiver receiver : receivers) {
            try {
                emailService.sendEmail(receiver.getEmail(), "Mensaje de " + message.getCustomer().getName(),
                        messageBody);

                System.out.println("Email enviado a: " + receiver.getEmail());
            } catch (Exception e) {
                System.out.println("Error al notificar por email a: " + receiver.getEmail() + " - " + e.getMessage());
            }
        }
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
