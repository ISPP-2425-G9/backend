package com.caronte.caronte.deathCertificate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituaryRepository;
import com.caronte.caronte.util.MediaHandler;

@Service
public class DeathCertificateService {

    DeathCertificateRepository deathCertificateRepository;
    ObituaryRepository obituaryRepository;

    public DeathCertificateService(DeathCertificateRepository deathCertificateRepository, ObituaryRepository obituaryRepository) {
        this.deathCertificateRepository = deathCertificateRepository;
        this.obituaryRepository = obituaryRepository;
    }


    @Transactional
    public DeathCertificate createDeathCertificateAndRelations(DeathCertificateRequestDTO request) {
        Iterable<Obituary> obituaries = obituaryRepository.findByCustomerDni(request.getDni());
        if (!obituaries.iterator().hasNext()) {
            throw new IllegalArgumentException("There are no obituaries with that ID number.");        
        }
        if(obituaries.iterator().next().getDeathCertificate() != null){
            throw new CertificateAssociationException("The death certificate has already been uploaded.");
        }
        DeathCertificate certificate = createDeathCertificate(request);

        for (Obituary obituary : obituaries) {
            obituary.setDeathCertificate(certificate);
            obituaryRepository.save(obituary);
        }
        return certificate;
    }

    @Transactional
    public DeathCertificate createDeathCertificate(DeathCertificateRequestDTO request) {
        String certificate = request.getFile();
        if (certificate != null && certificate.startsWith("data:image/") ) {
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

    @Transactional
    public DeathCertificate getAllDeathCertificates() {
        return deathCertificateRepository.findAll().iterator().next();
    }

    @Transactional
    public DeathCertificateWithObituaryDniDTO getDeathCertificateByObituaryId(Long obituaryId) {
        Obituary obituary = obituaryRepository.findById(obituaryId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The obituary has not been found"));

        if (obituary.getDeathCertificate() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Death certificate not found");
        }

        return new DeathCertificateWithObituaryDniDTO(obituary.getCustomer().getDni(), obituary.getDeathCertificate());
    }


    

}
