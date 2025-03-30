package com.caronte.caronte.obituary;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.deathCertificate.DeathCertificateService;
import com.caronte.caronte.deathCertificate.DTOs.DeathCertificateRequestDTO;
import com.caronte.caronte.imageTemplate.ImageTemplate;
import com.caronte.caronte.imageTemplate.ImageTemplateRepository;
import com.caronte.caronte.obituary.DTOs.ObituraryRequestDto;
import com.caronte.caronte.receiver.Receiver;
import com.caronte.caronte.receiver.ReceiverRepository;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.util.exceptions.ResourceNotFound;
import com.caronte.caronte.util.exceptions.ResponseThrow;

@Service
public class ObituaryService {

    private final ObituaryRepository obituaryRepository;
    private final CustomerRepository customerRepository;
    private final ImageTemplateRepository imageTemplateRepository;
    private final ReceiverRepository receiverRepository;
    private final DeathCertificateService deathCertificateService;
    private final MediaHandler mediaHandler;

    public ObituaryService(ObituaryRepository obituaryRepository, CustomerRepository customerRepository, 
            ReceiverRepository receiverRepository, ImageTemplateRepository imageTemplateRepository,
            DeathCertificateService deathCertificateService, MediaHandler mediaHandler) {
        this.receiverRepository = receiverRepository;
        this.customerRepository = customerRepository;
        this.obituaryRepository = obituaryRepository;
        this.imageTemplateRepository = imageTemplateRepository;
        this.deathCertificateService = deathCertificateService;
        this.mediaHandler = mediaHandler;
    }

    
    @Transactional(readOnly = true)
    public Obituary findById(Long id) {
        return obituaryRepository.findById(id).orElseThrow(() -> ResourceNotFound.of("Obituary"));
    }
    
    @Transactional(readOnly = true)
    public List<Obituary> findObituaryByCustomerDni(String dni) {
        return obituaryRepository.findByCustomerDni(dni);
    }

    @Transactional
    public DeathCertificate certificateManagement(ObituraryRequestDto request, Customer customer, Long customerId) {
        DeathCertificateRequestDTO deathCertificateDTO = request.getDeathCertificate();
        String dni = deathCertificateDTO.getDni();
        
        ResponseThrow.checkOrBadRequest(dni != null && deathCertificateDTO != null && deathCertificateDTO.getFile() != null, 
                                 "The Death Certificate is invalid");
        ResponseThrow.checkOrBadRequest(!customer.hasDni(deathCertificateDTO.getDni()), "No puedes subir un certificado con tu DNI");

        List<Obituary> obituaries = obituaryRepository.findByCustomerDni(dni);
        Boolean existCustomer = customerRepository.existsByDni(dni);

        DeathCertificate deathCertificate = existCustomer && !obituaries.isEmpty() && obituaries.getFirst().getDeathCertificate() == null ?
            deathCertificateService.createDeathCertificateAndRelations(deathCertificateDTO, customerId ) :
            deathCertificateService.createDeathCertificate(deathCertificateDTO);  
        
        return deathCertificate;
    }

    @Transactional
    public Obituary createObituaryWithReceivers(ObituraryRequestDto request, Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> ResourceNotFound.of("Customer"));

        ImageTemplate imageTemplate = imageTemplateRepository.findById(request.getImageTemplate_id()).orElseThrow(() -> ResourceNotFound.of("Image template"));
        DeathCertificate deathCertificate = null;

        if (!request.getIsMine()) {
            deathCertificate = certificateManagement(request, customer, customerId);
        } else {
            request.setDeathDate(null);
        }


        String customUrl = request.getCustomImage();
        String customImageUrl = Objects.nonNull(customUrl) && customUrl.startsWith("data:image/") ?
                mediaHandler.uploadImageToCloudinary(customUrl, "obituaries") : customUrl;
            
        request.setDefaultWordColorIfNull();

        Obituary obituary = saveObituary(request, customImageUrl, customer, imageTemplate, deathCertificate);

        List<ObituraryRequestDto.ContactDto> contacts = request.getContacts();
        List<Receiver> receivers = contacts.stream().map(contactDto -> Receiver.parse(contactDto, obituary)).toList();
        receiverRepository.saveAll(receivers);

        return obituary;
    }

    @Transactional
    public Obituary updateObituaryWithReceivers(Long customerId, Long obituaryId, ObituraryRequestDto request) {
        Obituary obituary = findById(obituaryId);

        ResponseThrow.checkOrBadRequest(request.getIsMine(), "You can't upload the obituary since it isn't yours");
        ResponseThrow.checkOrBadRequest(!obituary.isVerified(), "You can't upload the obituary since the death certificate is verified");
        ResponseThrow.checkOrBadRequest(obituary.hasCustomerId(customerId), "You are not allowed to update this obituary");
        ResponseThrow.checkOrBadRequest(obituary.getIsMine() == request.getIsMine(), "You can't change IsMine property");

        String customUrl = request.getCustomImage();
        ImageTemplate imageTemplate = imageTemplateRepository.findById(request.getImageTemplate_id()).orElseThrow(() -> ResourceNotFound.of("Image template"));
        String customImageUrl = customUrl != null && customUrl.startsWith("data:image/") ?
            mediaHandler.uploadImageToCloudinary(customUrl, "obituaries") : customUrl;
        
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> ResourceNotFound.of("Customer"));
        obituary.update(request);
        obituary.setCustomImageUrl(customImageUrl);
        obituary.setImageTemplate(imageTemplate);
        obituary.setCustomer(customer);

        Obituary updatedObituary = updateObituary(obituary);

        receiverRepository.deleteByObituary(obituary);
        receiverRepository.flush();

        List<Receiver> receivers = request.getContacts().stream().map(contactDto -> Receiver.parse(contactDto, obituary)).toList();
        receiverRepository.saveAll(receivers);

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
        obituary = obituaryRepository.saveAndFlush(obituary);
        return obituary;
    }
  
    @Transactional
    public void deleteObituaryByCustomer(Long customerId, Long obituaryId) {
        Obituary obituary = findById(obituaryId);
        ResponseThrow.checkOrForbidden(obituary.hasCustomerId(customerId));
        obituaryRepository.deleteById(obituaryId);
    }

    @Transactional
    public Obituary updateObituary(Obituary obituary) {
        Obituary updatedObituary = obituaryRepository.saveAndFlush(obituary);
        return updatedObituary;
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
            obituary.setDefaultWordColorIfNull();
        });
        return obituaries;
    }

    @Transactional(readOnly = true)
    public Obituary getObituaryById(Long obituaryId, Long userId) {
        Obituary obituary = obituaryRepository.findById(obituaryId).orElseThrow(() -> ResourceNotFound.of("Obituary"));
        ResponseThrow.checkOrForbidden(obituary.hasCustomerId(userId));
        obituary.setDefaultWordColorIfNull();
        return obituary;

    }


}