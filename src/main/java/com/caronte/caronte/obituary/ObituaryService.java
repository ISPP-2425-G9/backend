package com.caronte.caronte.obituary;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.deathCertificate.DeathCertificateService;
import com.caronte.caronte.imageTemplate.ImageTemplate;
import com.caronte.caronte.imageTemplate.ImageTemplateService;
import com.caronte.caronte.receiver.ReceiverService;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.util.exceptions.ResourceNotFound;
import com.caronte.caronte.util.exceptions.ResponseThrow;

@Service
public class ObituaryService {

    ObituaryRepository obituaryRepository;
    CustomerRepository customerRepository;
    ImageTemplateService imageTemplateService;
    ReceiverService receiverService;
    DeathCertificateService deathCertificateService;
    MediaHandler mediaHandler;

    public ObituaryService(ObituaryRepository obituaryRepository, CustomerRepository customerRepository,
            ImageTemplateService imageTemplateService, ReceiverService receiverService,
            DeathCertificateService deathCertificateService, MediaHandler mediaHandler) {
        this.receiverService = receiverService;
        this.customerRepository = customerRepository;
        this.imageTemplateService = imageTemplateService;
        this.obituaryRepository = obituaryRepository;
        this.deathCertificateService = deathCertificateService;
        this.mediaHandler = mediaHandler;
    }

    @Transactional
    public Obituary createObituaryWithReceivers(ObituraryRequestDto request, Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> ResourceNotFound.of("Customer"));

        ImageTemplate imageTemplate = imageTemplateService.findById(request.getImageTemplate_id());
        DeathCertificate deathCertificate = null;

        if (!request.getIsMine()) {
            if (request.getDeathCertificate() == null || request.getDeathCertificate().getDni() == null
                    || request.getDeathCertificate().getFile() == null) {
                throw new IllegalArgumentException("The Death Certificate is invalid");
            }
            if (Objects.equals(customer.getDni(), request.getDeathCertificate().getDni())) {
                throw new IllegalArgumentException("No puedes subir un certificado con tu DNI");
            }
            
            List<Obituary> obituaries = obituaryRepository.findByCustomerDni(request.getDeathCertificate().getDni());
            Boolean existCustomer = customerRepository.existsByDni(request.getDeathCertificate().getDni());

            deathCertificate = existCustomer && !obituaries.isEmpty() && obituaries.getFirst().getDeathCertificate() == null ?
                deathCertificateService.createDeathCertificateAndRelations(request.getDeathCertificate(), customerId):
                deathCertificateService.createDeathCertificate(request.getDeathCertificate());
        }
        String customUrl = request.getCustomImage();
        String customImageUrl = Objects.nonNull(customUrl) && customUrl.startsWith("data:image/") && false ?
                mediaHandler.uploadImageToCloudinary(customUrl, "obituaries") : customUrl;
        customImageUrl = "";
        Obituary obituary = saveObituary(request, customImageUrl, customer, imageTemplate, deathCertificate);

        List<ObituraryRequestDto.ContactDto> contacts = request.getContacts();
        receiverService.saveAllObituaryReceiver(contacts, obituary);

        return obituary;
    }

    @Transactional
    public Obituary updateObituaryWithReceivers(Long customerId, Long obituaryId, ObituraryRequestDto request) {
        Obituary obituary = findById(obituaryId);

        if (!Objects.equals(obituary.getCustomer().getId(), customerId)) {
            throw new IllegalArgumentException("You are not allowed to update this obituary");
        }

        if (obituary.getIsMine() != request.getIsMine()) {
            throw new IllegalArgumentException("You can't change IsMine property");
        }

        if (!request.getIsMine() && obituary.getDeathCertificate().getIsVerified()) {
            throw new IllegalArgumentException("You can't upload the obituary since the death certificate is verified");
        }

        String customUrl = request.getCustomImage();
        ImageTemplate imageTemplate = imageTemplateService.findById(request.getImageTemplate_id());

        String customImageUrl = customUrl != null && customUrl.startsWith("data:image/") && false ?
            mediaHandler.uploadImageToCloudinary(customUrl, "obituaries") : customUrl;
        
        Obituary updatedObituary = request.parse();
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> ResourceNotFound.of("Customer"));
        updatedObituary.setId(obituaryId);
        updatedObituary.setCustomImageUrl(customImageUrl);
        updatedObituary.setImageTemplate(imageTemplate);
        updatedObituary.setCustomer(customer);

        updatedObituary = updateObituary(updatedObituary);

        receiverService.deleteReceiversByObituaryId(updatedObituary);

        List<ObituraryRequestDto.ContactDto> contacts = request.getContacts();
        receiverService.saveAllObituaryReceiver(contacts, updatedObituary);

        return updatedObituary;
    }

    @Transactional
    public Obituary saveObituary(ObituraryRequestDto obituraryRequestDto, String customImageUrl, Customer customer,
            ImageTemplate imageTemplate, DeathCertificate certificate) {
        Obituary obituary = obituraryRequestDto.parse();
        obituary.setCustomImageUrl(customImageUrl);
        obituary.setCustomer(customer);
        obituary.setImageTemplate(imageTemplate);
        obituary.setDeathCertificate(certificate);
        return obituaryRepository.save(obituary);
    }

    @Transactional
    public void deleteObituaryByCustomer(Long customerId, Long obituaryId) {
        Obituary obituary = findById(obituaryId);
        ResponseThrow.checkOrForbidden(!Objects.equals(obituary.getCustomer().getId(), customerId));
        obituaryRepository.deleteById(obituaryId);
    }

    @Transactional(readOnly = true)
    public Obituary findById(Long id) {
        return obituaryRepository.findById(id).orElseThrow(() -> ResourceNotFound.of("Obituary"));
    }

    @Transactional
    public Obituary updateObituary(Obituary obituary) {
        return obituaryRepository.save(obituary);
    }

    @Transactional
    public void deleteObituary(Long id) {
        obituaryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Obituary> getAllObituariesByCustomer(Long customerId) {
        List<Obituary> obituaries = obituaryRepository.findByCustomerId(customerId);
        obituaries.forEach(obituary -> {
            obituary.setCustomer(null);
            if (obituary.getWordColor() == null) {
                obituary.setWordColor("0,0,0");
            }
        });
        return obituaries;
    }

    @Transactional(readOnly = true)
    public Obituary getObituaryById(Long obituaryId, Long userId) {
        Obituary obituary = obituaryRepository.findById(obituaryId).orElseThrow(() -> ResourceNotFound.of("Obituary"));
        ResponseThrow.checkOrForbidden(Objects.equals(obituary.getCustomer().getId(), userId));
        if (obituary.getWordColor() == null) {
            obituary.setWordColor("0,0,0");
        }
        return obituary;

    }

    @Transactional(readOnly = true)
    public List<Obituary> findObituaryByCustomerDni(String dni) {
        return obituaryRepository.findByCustomerDni(dni);
    }

}
