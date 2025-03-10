package com.caronte.caronte.receiver;

import java.util.List;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.caronte.caronte.obituary.ObituraryRequestDto;
import com.caronte.caronte.obituary.Obituary;

@Service
public class ReceiverService {

    private final ReceiverRepository receiverRepository;
    private final JavaMailSender mailSender;

    public ReceiverService(ReceiverRepository receiverRepository, JavaMailSender mailSender) {
        this.receiverRepository = receiverRepository;
        this.mailSender = mailSender;
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

    public void notifyReceivers(List<ObituraryRequestDto.ContactDto> contacts, Obituary obituary) {
        for (ObituraryRequestDto.ContactDto contact : contacts) {
            String subject = "Esquela de " + obituary.getName();
            String body = "Hola " + contact.getName() + ",\n\n"
                    + "Te compartimos la esquela de " + obituary.getName() + ":\n"
                    + "Frase de despedida: " + obituary.getFarewellPhrase() + "\n"
                    + "Mensaje: " + obituary.getFarewellMessage() + "\n\n"
                    + "Un saludo,\nCARONTE";
            System.out.println("Enviando email a: " + contact.getEmail());
            try {
                sendEmail(contact.getEmail(), subject, body);
                System.out.println("Email enviado a: " + contact.getEmail());
            } catch (Exception e) {
                System.out.println("Error al notificar por email a: " + contact.getEmail() + " - " + e.getMessage());
            }
        }
    }

    public void sendEmail(String to, String subject, String body) {
        System.out.println("Enviando email a: " + to);
        System.out.println("Asunto: " + subject);
        System.out.println("Mensaje: " + body);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
    
}
