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
    public void createDeathCertificate(DeathCertificateRequestDTO request) {

        String certificate = request.getFile();
        if (certificate != null && certificate.startsWith("data:image/") ) {
            certificate = MediaHandler.uploadImageToCloudinary(MediaHandler.base64ToImage(certificate), "certificates");
        } else {
            throw new IllegalArgumentException("El certificado de defunción no es una imagen válida");
        }
        Iterable<Obituary> obituaries = obituaryRepository.findByCustomerDni(request.getDni());
        if (!obituaries.iterator().hasNext()) {
            throw new IllegalArgumentException("No existen esquelas con ese DNI");        
        }
        if(obituaries.iterator().next().getDeathCertificate() != null){
            throw new CertificateAssociationException("El certificado de defunción ya ha sido subido");
        }

        DeathCertificate deathCertificate = new DeathCertificate();
        deathCertificate.setUrl(certificate);
        deathCertificate.setIsVerified(false);

        deathCertificateRepository.save(deathCertificate);

        for (Obituary obituary : obituaries) {
            obituary.setDeathCertificate(deathCertificate);
            obituaryRepository.save(obituary);
        }
    }

    @Transactional
    public DeathCertificate getAllDeathCertificates() {
        return deathCertificateRepository.findAll().iterator().next();
    }

}
