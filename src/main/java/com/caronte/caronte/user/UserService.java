package com.caronte.caronte.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.UserChangePasswordRequest;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyRepository;
import com.caronte.caronte.company.CompanyService;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.customer.CustomerService;
import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.deathCertificate.DeathCertificateRepository;
import com.caronte.caronte.deathCertificate.DeathCertificateService;
import com.caronte.caronte.emergencyContact.EmergencyContact;
import com.caronte.caronte.emergencyContact.EmergencyContactRepository;
import com.caronte.caronte.image.Image;
import com.caronte.caronte.image.ImageRepository;
import com.caronte.caronte.message.Message;
import com.caronte.caronte.message.MessageRepository;
import com.caronte.caronte.message.MessageService;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituaryRepository;
import com.caronte.caronte.obituary.ObituaryService;
import com.caronte.caronte.receiver.Receiver;
import com.caronte.caronte.receiver.ReceiverRepository;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.util.exceptions.ResourceNotFound;
import com.caronte.caronte.util.exceptions.ResponseThrow;
import com.stripe.exception.StripeException;
import com.stripe.model.tax.Registration.CountryOptions.Me;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final ObituaryRepository obituaryRepository;
    private final MessageRepository messageRepository;
    private final ImageRepository imageRepository;
    private final ReceiverRepository receiverRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final DeathCertificateRepository deathCertificateRepository;
    private final CompanyRepository companyRepository; 
    private final MediaHandler mediaHandler;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            CustomerRepository customerRepository, ObituaryRepository obituaryRepository, MessageRepository messageRepository,
            ImageRepository imageRepository, ReceiverRepository receiverRepository, EmergencyContactRepository emergencyContactRepository,
            DeathCertificateRepository deathCertificateRepository, CompanyRepository companyRepository,MediaHandler mediaHandler) { 
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
        this.obituaryRepository = obituaryRepository;
        this.messageRepository = messageRepository;
        this.imageRepository = imageRepository;
        this.receiverRepository = receiverRepository;
        this.emergencyContactRepository = emergencyContactRepository;
        this.deathCertificateRepository = deathCertificateRepository;
        this.companyRepository = companyRepository;
        this.mediaHandler = mediaHandler;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> ResourceNotFound.of("User", "ID", id));
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public User findCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ResponseThrow.checkOrForbidden(auth.isAuthenticated(), "User is not authenticated");
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return userRepository.findById(userDetails.getId()).orElseThrow(() -> ResourceNotFound.of("User"));
    }

    @Transactional(readOnly = true)
    public Long findCurrentUserId() {
        return findCurrentUser().getId();
    }

    @Transactional(readOnly = true)
    public String findCurrentUserEmail() {
        return findCurrentUser().getEmail();
    }


    @Transactional(readOnly = true)
    public User authorizeUserOrAdmin(Long userId, String message){
        User user = findCurrentUser();
        UserDetailsImpl auth =  (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ResponseThrow.checkOrBadRequest(user.getId().equals(userId) || auth.isAdmin(), message);
        return user;
    }

    @Transactional(readOnly = true)
    public User authorizeUserOrAdmin(Long id){
        return authorizeUserOrAdmin(id, "No puedes realizar acciones en la cuenta de otro usuario");
    }


    @Transactional(readOnly = true)
    public User authorizeUser(Long userId, String message){
        User user = findCurrentUser();
        ResponseThrow.checkOrBadRequest(user.getId().equals(userId), message);
        return user;
    }

    @Transactional(readOnly = true)
    public User authorizeUser(Long id){
        return authorizeUser(id, "No puedes realizar acciones en la cuenta de otro usuario");
    }

    @Transactional
    public void delete(Long id) throws StripeException {
        User user = userRepository.findById(id).orElseThrow(() -> ResourceNotFound.of("User"));
        user.getPlan().cancel();
        anonymizeData(id);
    }

    @Transactional
    public User changePassword(Long id, UserChangePasswordRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> ResourceNotFound.of("User"));
        user.setPassword(this.passwordEncoder.encode(request.getNewPassword()));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void authorizeAdmin(String message){
        UserDetailsImpl auth =  (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ResponseThrow.checkOrBadRequest(auth.isAdmin(), message);
    }

    private static final String ANONYMOUS = "anonimo";

@Transactional
void anonymizeData(Long id) {
     User user = userRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFound("User", "ID", id));
    List<String> deteleUrls = new ArrayList<>();
    if (user instanceof Customer customer) {
        List<Obituary> obituaries = obituaryRepository.findByCustomerId(id);
        List<Receiver> allReceivers = new ArrayList<>();
        List<Image> allImages = new ArrayList<>();
        for (Obituary o : obituaries) {
            deteleUrls.add(o.getCustomImageUrl());
            o.setName(ANONYMOUS);
            o.setFarewellMessage(ANONYMOUS);
            o.setFarewellPhrase(ANONYMOUS);
            o.setCustomImageUrl(ANONYMOUS);

            List<Receiver> receivers = receiverRepository.findByObituary(o);
            int count = 0;
            for (Receiver r : receivers) {
                r.setName(ANONYMOUS);
                r.setTelephone(ANONYMOUS+ count);
                r.setEmail(ANONYMOUS + count);
                allReceivers.add(r);
                count++;
            }
        }
        List<Message> messages = messageRepository.findAllByCustomerId(id);
        for (Message m : messages) {
            m.setTitle(ANONYMOUS);
            m.setBody(ANONYMOUS);
            List<Receiver> receivers = receiverRepository.findByMessageId(m.getId());
            int count = 0;
            for (Receiver r : receivers) {
                r.setName(ANONYMOUS);
                r.setTelephone(ANONYMOUS+ count);
                r.setEmail(ANONYMOUS+ count);
                allReceivers.add(r);
                count++;
            }
            List<Image> images = imageRepository.findAllByMessageId(m.getId());
            for (Image i : images) {
                deteleUrls.add(i.getImageUrl());
                i.setImageUrl(ANONYMOUS);
                allImages.add(i);
            }
        }
        List<DeathCertificate> deathCertificates = deathCertificateRepository.findAllByDni(customer.getDni());
        for (DeathCertificate d : deathCertificates) {
            deteleUrls.add(d.getUrl());
            d.setUrl(ANONYMOUS);
            d.setDni(ANONYMOUS);
        }
        List<EmergencyContact> emergencyContacts = emergencyContactRepository.findAllByCustomerEmail(customer.getEmail());
        for (EmergencyContact e : emergencyContacts) {
            e.setName(ANONYMOUS);
            e.setTelephone(ANONYMOUS);
            e.setEmail(ANONYMOUS);
        }
        customer.setName(ANONYMOUS);
        customer.setEmail(ANONYMOUS);
        customer.setTelephone(ANONYMOUS);
        customer.setPassword(ANONYMOUS);
        customer.setDni(ANONYMOUS);

        obituaryRepository.saveAll(obituaries);
        messageRepository.saveAll(messages);
        receiverRepository.saveAll(allReceivers);
        imageRepository.saveAll(allImages);
        deathCertificateRepository.saveAll(deathCertificates);
        emergencyContactRepository.saveAll(emergencyContacts);
        customerRepository.saveAndFlush(customer);

        removeImages(deteleUrls);

    } else if (user instanceof Company company) {
        deteleUrls.add(company.getImageUrl());
        company.setName(ANONYMOUS);
        company.setEmail(ANONYMOUS);
        company.setTelephone(ANONYMOUS);
        company.setPassword(ANONYMOUS);
        company.setAddress(ANONYMOUS);
        company.setCity(ANONYMOUS);
        company.setNif(ANONYMOUS);
        company.setZipCode("00000");
        company.setDescription(ANONYMOUS);
        company.setImageUrl(ANONYMOUS);
        companyRepository.saveAndFlush(company);
        removeImages(deteleUrls);

    }
}

private void removeImages(List<String> existingImages) {
    existingImages.forEach(mediaHandler::deleteImageFromCloudinary);
}

    

}
