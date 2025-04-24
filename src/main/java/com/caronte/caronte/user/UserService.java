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
import com.caronte.caronte.util.Hash;
import com.caronte.caronte.util.MediaHandler;
import com.caronte.caronte.util.exceptions.ResourceNotFound;
import com.caronte.caronte.util.exceptions.ResponseThrow;
import com.stripe.exception.StripeException;
import com.stripe.model.tax.Registration.CountryOptions.Me;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Hash hash;
    private final CustomerRepository customerRepository;
    private final ObituaryRepository obituaryRepository;
    private final MessageRepository messageRepository;
    private final ImageRepository imageRepository;
    private final ReceiverRepository receiverRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final DeathCertificateRepository deathCertificateRepository;
    private final CompanyRepository companyRepository; 
    private final MediaHandler mediaHandler;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, Hash hash,
            CustomerRepository customerRepository, ObituaryRepository obituaryRepository, MessageRepository messageRepository,
            ImageRepository imageRepository, ReceiverRepository receiverRepository, EmergencyContactRepository emergencyContactRepository,
            DeathCertificateRepository deathCertificateRepository, CompanyRepository companyRepository,MediaHandler mediaHandler) { 
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.hash = hash;
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

    @Transactional void anonymizeData(Long id){
        Optional<Customer> customerOpt = customerRepository.findById(id);
        Optional<Company>  companyOpt = companyRepository.findById(id);
        List<String> deteleUrls = new ArrayList<>();

        if(customerOpt.isPresent()){
            Customer customer = customerOpt.get();
            List<Obituary> obituaries = obituaryRepository.findByCustomerId(id);
            for (Obituary o : obituaries){
                deteleUrls.add(o.getCustomImageUrl());
                o.setName("Anónimo");
                o.setFarewellMessage("Anónimo");
                o.setFarewellPhrase("Anonimo");
                o.setCustomImageUrl("Anonimo");
                obituaryRepository.save(o);
                obituaryRepository.flush();
                List<Receiver> receivers = receiverRepository.findByObituary(o);
                Integer count = 0;
                for (Receiver r : receivers){
                    r.setName("Anónimo");
                    r.setTelephone("00000000"+count);
                    r.setEmail("anonimo"+hash.hash(r.getEmail())+".com");
                    count++;
                    receiverRepository.save(r);
                    receiverRepository.flush();
                }
            }
            List<Message> messages = messageRepository.findAllByCustomerId(id);
            for (Message m : messages){
                m.setTitle("Anónimo");
                m.setBody("Anónimo");
                messageRepository.save(m);
                messageRepository.flush();
                List<Receiver> receivers = receiverRepository.findByMessageId(m.getId());
                Integer count = 0;
                for (Receiver r : receivers){
                    r.setName("Anónimo");
                    r.setTelephone("00000000"+count);
                    r.setEmail("anonimo"+hash.hash(r.getEmail())+".com");
                    count++;
                    receiverRepository.save(r);
                    receiverRepository.flush();
                }
                List<Image> images = imageRepository.findAllByMessageId(m.getId());
                for (Image i : images){
                    deteleUrls.add(i.getImageUrl());
                    i.setImageUrl("Anonimo");
                    imageRepository.save(i);
                    imageRepository.flush();
                }
            }

            List<DeathCertificate> deathCertificate = deathCertificateRepository.findAllByDni(customer.getDni());
            for(DeathCertificate d : deathCertificate){
                deteleUrls.add(d.getUrl());
                d.setUrl("Anonimo");
                d.setDni("00000000A");
                deathCertificateRepository.save(d);
                deathCertificateRepository.flush();
            }
            List<EmergencyContact> emergencyContacts = emergencyContactRepository.findAllByCustomerEmail(customer.getEmail());
            Integer count = 0;
            for (EmergencyContact e : emergencyContacts){
                e.setName("Anónimo");
                e.setTelephone("00000000"+count);
                e.setEmail("anonimo"+hash.hash(e.getEmail())+".com");
                count++;
                emergencyContactRepository.save(e);
                emergencyContactRepository.flush();
            }
            customer.setName("Anónimo");
            customer.setEmail("anonimo"+hash.hash(customer.getEmail())+".com");
            customer.setTelephone("000000000");
            customer.setPassword("anonimo"+hash.hash(customer.getPassword()));
            customer.setDni(hash.hash(customer.getDni()));
            customerRepository.save(customer);
            customerRepository.flush();
            System.out.println(deteleUrls);
            removeImages(deteleUrls);

        }
        else if(companyOpt.isPresent()){
            Company company = companyOpt.get();
            deteleUrls.add(company.getImageUrl());
            company.setName("Anónimo");
            company.setEmail("anonimo"+hash.hash(company.getEmail())+".com");
            company.setTelephone("000000000");
            company.setPassword("anonimo"+hash.hash(company.getPassword()));
            company.setAddress("Anónimo");
            company.setCity("Anónimo");
            company.setNif(hash.hash(company.getNif()));
            company.setZipCode("00000");
            company.setDescription("Anónimo");
            company.setImageUrl("Anonimo");
            companyRepository.save(company);
            companyRepository.flush();
            removeImages(deteleUrls);
            
        }
        else{
            throw new ResourceNotFound("User", "ID", id);
        }
    }
    private void removeImages(List<String> existingImages) {
        for (String image : existingImages) {
            mediaHandler.deleteImageFromCloudinary(image);
        }
    }

}
