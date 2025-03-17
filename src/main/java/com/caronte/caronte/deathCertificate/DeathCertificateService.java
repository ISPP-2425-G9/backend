package com.caronte.caronte.deathCertificate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String certificate = request.getCertificate();
        if (certificate != null && certificate.startsWith("data:image/") ) {
            certificate = MediaHandler.uploadImageToCloudinary(MediaHandler.base64ToImage(certificate), "certificates");
        } else {
            throw new IllegalArgumentException("El certificado de defunción no es una imagen válida");
        }
        DeathCertificate deathCertificate = new DeathCertificate();
        deathCertificate.setUrl(certificate);
        deathCertificate.setIsVerified(false);

        deathCertificateRepository.save(deathCertificate);
        
        Iterable<Obituary> obituaries = obituaryRepository.findByCustomerDni(request.getDni());

        for (Obituary obituary : obituaries) {
            obituary.setDeathCertificate(deathCertificate);
            obituaryRepository.save(obituary);
        }
    }

}
