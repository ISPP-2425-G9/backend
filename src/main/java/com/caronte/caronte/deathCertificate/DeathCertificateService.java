package com.caronte.caronte.deathCertificate;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituaryRepository;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.util.exceptions.CertificateAssociationException;
import com.caronte.caronte.util.exceptions.ResponseThrow;

@Service
public class DeathCertificateService {

    DeathCertificateRepository deathCertificateRepository;
    ObituaryRepository obituaryRepository;
    CustomerRepository customerRepository;

    public DeathCertificateService(DeathCertificateRepository deathCertificateRepository, ObituaryRepository obituaryRepository, CustomerRepository customerRepository) {
        this.deathCertificateRepository = deathCertificateRepository;
        this.obituaryRepository = obituaryRepository;
        this.customerRepository = customerRepository;
    }


    @Transactional
    public DeathCertificate createDeathCertificateAndRelations(DeathCertificateRequestDTO request, Long customerId) {
        checkDeathCertificate(request, customerId);
        DeathCertificate certificate = createDeathCertificate(request);
        List<Obituary> obituaries = obituaryRepository.findByCustomerDni(request.getDni());
        for (Obituary obituary : obituaries) {
            obituary.setDeathCertificate(certificate);
            obituaryRepository.save(obituary);
        }
        return certificate;
    }

    @Transactional
    public void checkDeathCertificate(DeathCertificateRequestDTO request, Long customerId) {
        Customer customerLogged = null;
        if (customerId != null){
            customerLogged = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The customer has not been found"));
        }
        if(Objects.nonNull(customerLogged) && Objects.equals(customerLogged.getDni(), request.getDni())){
            throw new IllegalArgumentException("No puedes subir un certificado de defunción con tu DNI");
        }
        List<Obituary> obituaries = obituaryRepository.findByCustomerDni(request.getDni());
        if (obituaries.isEmpty()) {
            throw new IllegalArgumentException("No hay esquelas creadas asociadas a ese DNI");        
        }
        if(obituaries.getFirst().getDeathCertificate() != null){
            throw new CertificateAssociationException("El certificado de este cliente ya ha sido subido");
        }
    }

    @Transactional
    public DeathCertificate createDeathCertificate(DeathCertificateRequestDTO request) {
        String certificate = request.getFile();
        if (Objects.nonNull(certificate) && certificate.startsWith("data:image/") ) {
            certificate = MediaHandler.uploadImageToCloudinary(MediaHandler.base64ToImage(certificate), "certificates");
        } else {
            throw new IllegalArgumentException("The death certificate is not a valid image");
        }
        DeathCertificate deathCertificate = new DeathCertificate();
        deathCertificate.setUrl(certificate);
        deathCertificate.setIsVerified(false);

        deathCertificateRepository.save(deathCertificate);
        return deathCertificate; 
    }

    @Transactional(readOnly = true)
    public List<DeathCertificate> getAllDeathCertificates() {
        return deathCertificateRepository.findAll();
    }

    @Transactional
    public DeathCertificateWithObituaryDniDTO getDeathCertificateByObituaryId(Long obituaryId) {
        Obituary obituary = obituaryRepository.findById(obituaryId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The obituary has not been found"));
        ResponseThrow.checkOrNotFound(Objects.isNull(obituary.getDeathCertificate()), "Death certificate not found");
        return new DeathCertificateWithObituaryDniDTO(obituary.getCustomer().getDni(), obituary.getDeathCertificate());
    }


    

}
