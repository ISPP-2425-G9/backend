package com.caronte.caronte.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.admin.DTOs.CertificateResponseDTO;
import com.caronte.caronte.admin.DTOs.MessageResponseDTO;
import com.caronte.caronte.admin.DTOs.ObituaryResponseDTO;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.customer.CustomerService;
import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.deathCertificate.DeathCertificateRepository;
import com.caronte.caronte.image.Image;
import com.caronte.caronte.image.ImageRepository;
import com.caronte.caronte.message.Message;
import com.caronte.caronte.message.MessageRepository;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituaryRepository;
import com.caronte.caronte.obituary.ObituaryService;
import com.caronte.caronte.user.UserService;


@Service
public class AdminService {

    private final DeathCertificateRepository deathCertificateRepository;
    private final ObituaryRepository obituaryRepository;
    private final UserService userService;
    private final ObituaryService obituaryService;
    private final MessageRepository messageRepository;
    private final ImageRepository imageRepository;


    public AdminService(DeathCertificateRepository deathCertificateRepository, 
        ObituaryRepository obituaryRepository, UserService userService, 
        CustomerRepository customerRepository, ObituaryService obituaryService,
        MessageRepository messageRepository,
        ImageRepository imageRepository) {

        this.imageRepository = imageRepository;
        this.messageRepository = messageRepository;
        this.obituaryRepository = obituaryRepository;
        this.deathCertificateRepository = deathCertificateRepository;      
        this.userService = userService;
        this.obituaryService = obituaryService;
    }


    @Transactional
    public List<CertificateResponseDTO> getAllPendingCertificates() {
        userService.authorizeAdmin("User is not admin");
        List<DeathCertificate> pendingCertificates = deathCertificateRepository.getAllCertificatesByIsVerified(false);
        List<CertificateResponseDTO> response = new ArrayList();
        String dni = "";
        String name = "";

        for (DeathCertificate certificate : pendingCertificates) {

            CertificateResponseDTO certificateResponse = new CertificateResponseDTO();
            certificateResponse.setId(certificate.getId());
            certificateResponse.setCertificateUrl(certificate.getUrl());
            Customer customer = obituaryService.getCustomerByCertificateId(certificate.getId());
            if(customer != null){
                dni = customer.getDni();
                name = customer.getName();
            }
            certificateResponse.setDni(dni);
            certificateResponse.setName(name);
            response.add(certificateResponse);
        }
        return response;
    }

    @Transactional
    public CertificateResponseDTO getCertificateById(Long id) {
        userService.authorizeAdmin("User is not admin");
        return deathCertificateRepository.getCertificateById(id);
    }

    @Transactional
    public void verifyCertificate(Long id) {
        DeathCertificate deathCertificate = deathCertificateRepository.findById(id).orElse(null);
        if (deathCertificate != null) {
            deathCertificate.setIsVerified(true);
            deathCertificateRepository.save(deathCertificate);
        }
    }

    @Transactional
    public List<MessageResponseDTO> getAllMessagesByCertificateId(Long deathCertificateId) {
        userService.authorizeAdmin("User is not admin");
        Customer customer = obituaryService.getCustomerByCertificateId(deathCertificateId);
        List<MessageResponseDTO> response = new ArrayList<>();
        List<Message> messagesFromDB = messageRepository.findAllByCustomerId(customer.getId());

        for (Message message : messagesFromDB) {
            List<Image> images = imageRepository.findAllByMessageId(message.getId());
            MessageResponseDTO messageResponse = new MessageResponseDTO();
            messageResponse.setId(message.getId());
            messageResponse.setTitle(message.getTitle());
            messageResponse.setBody(message.getBody());
            messageResponse.setImages(images);
            response.add(messageResponse); 
        }
        return response;
    }

    
    @Transactional
    public List<ObituaryResponseDTO> getAllObituariesByCertificateId(Long deathCertificateId){
        userService.authorizeAdmin("User is not admin");
        List<ObituaryResponseDTO> obituariesResponse = new ArrayList<>();
        List<Obituary> obituaries = obituaryRepository.findByDeathCertificateId(deathCertificateId);
        for(Obituary obituary : obituaries) {
            ObituaryResponseDTO obituaryResponse = new ObituaryResponseDTO();
            obituaryResponse.setId(obituary.getId());
            obituaryResponse.setName(obituary.getName());
            obituaryResponse.setFarewellMessage(obituary.getFarewellMessage());
            obituaryResponse.setFarewellPhrase(obituary.getFarewellPhrase());
            obituariesResponse.add(obituaryResponse);
        }
        return null;
    }


    
    




}