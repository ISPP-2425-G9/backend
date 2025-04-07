package com.caronte.caronte.deathCertificate;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.deathCertificate.DTOs.DeathCertificateRequestDTO;
import com.caronte.caronte.deathCertificate.DTOs.DeathCertificateWithObituaryDniDTO;
import com.caronte.caronte.message.Message;
import com.caronte.caronte.message.MessageRepository;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituaryRepository;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.util.exceptions.CertificateAssociationException;
import com.caronte.caronte.util.exceptions.ResourceNotFound;
import com.caronte.caronte.util.exceptions.ResponseThrow;

@Service
public class DeathCertificateService {

    DeathCertificateRepository deathCertificateRepository;
    ObituaryRepository obituaryRepository;
    CustomerRepository customerRepository;
    MediaHandler mediaHandler;
    MessageRepository messageRepository;

    public DeathCertificateService(DeathCertificateRepository deathCertificateRepository, 
        ObituaryRepository obituaryRepository, CustomerRepository customerRepository,
        MediaHandler mediaHandler, MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.deathCertificateRepository = deathCertificateRepository;
        this.obituaryRepository = obituaryRepository;
        this.customerRepository = customerRepository;
        this.mediaHandler = mediaHandler;
    }
    
    @Transactional
    public DeathCertificate createDeathCertificateAndRelations(DeathCertificateRequestDTO request, Long customerId) {
        if(customerId != null) 
            checkDeathCertificate(request, customerId);
        DeathCertificate certificate = createDeathCertificate(request);
        List<Obituary> obituaries = obituaryRepository.findByCustomerDni(request.getDni());
        obituaries.forEach(obituary -> {
            if (obituary.getIsMine()) {
                obituary.setDeathCertificate(certificate);
            }
        });
        obituaryRepository.saveAll(obituaries);
        Customer customer = customerRepository.findByDni(request.getDni())
                .orElseThrow(() -> ResourceNotFound.of("Customer"));
        List<Message> messages = messageRepository.findAllByCustomerId(customer.getId());
        messages.forEach(message -> message.setDeathCertificate(certificate));
        messageRepository.saveAll(messages);
        return certificate;
    }

    @Transactional(readOnly = true)
    public void checkDeathCertificate(DeathCertificateRequestDTO request, Long customerId) {
        Customer customerLogged = customerRepository.findById(customerId).orElseThrow(() -> ResourceNotFound.of("Customer"));
        validateDniNotOwn(customerLogged, request);
        List<Obituary> obituaries = getObituariesByDni(customerLogged.getDni());
        validateObituaries(obituaries);
    }

    private void validateDniNotOwn(Customer customerLogged, DeathCertificateRequestDTO request) {
        ResponseThrow.checkOrBadRequest(!customerLogged.hasDni(request.getDni()), "No puedes subir un certificado de defunción con tu DNI");

    }
    
    private List<Obituary> getObituariesByDni(String dni) {
        return Optional.of(obituaryRepository.findByCustomerDni(dni))
            .filter(obituaries -> !obituaries.isEmpty())
            .orElseThrow(() -> new NoSuchElementException("No hay esquelas creadas asociadas a ese DNI"));
    }
    
    private void validateObituaries(List<Obituary> obituaries) {
        if (obituaries.getFirst().getDeathCertificate() != null)
            throw new CertificateAssociationException("El certificado de este cliente ya ha sido subido");
    }

    @Transactional
    public DeathCertificate createDeathCertificate(DeathCertificateRequestDTO request) {
        String certificate = Optional.ofNullable(request.getFile())
            .filter(file -> file.startsWith("data:image/"))
            .orElseThrow(() -> new IllegalArgumentException("The death certificate is not a valid image"));
        certificate = mediaHandler.uploadImageToCloudinary(certificate, "certificates");
        DeathCertificate deathCertificate = DeathCertificate.newDeathCertificate(certificate, request.getDni());
        deathCertificateRepository.save(deathCertificate);
        return deathCertificate; 
    }

    @Transactional(readOnly = true)
    public List<DeathCertificate> getAllDeathCertificates() {
        return deathCertificateRepository.findAll();
    }

    @Transactional
    public DeathCertificateWithObituaryDniDTO getDeathCertificateByObituaryId(Long obituaryId) {
        Obituary obituary = obituaryRepository.findById(obituaryId).orElseThrow(() -> ResourceNotFound.of("Obituary"));
        return new DeathCertificateWithObituaryDniDTO(obituary);
    }

}
