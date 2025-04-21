package com.caronte.caronte.user;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.UserChangePasswordRequest;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyService;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.customer.CustomerService;
import com.caronte.caronte.deathCertificate.DeathCertificate;
import com.caronte.caronte.deathCertificate.DeathCertificateService;
import com.caronte.caronte.message.Message;
import com.caronte.caronte.message.MessageService;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituaryRepository;
import com.caronte.caronte.obituary.ObituaryService;
import com.caronte.caronte.util.Hash;
import com.caronte.caronte.util.exceptions.ResourceNotFound;
import com.caronte.caronte.util.exceptions.ResponseThrow;
import com.stripe.exception.StripeException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Hash hash;
    private final CustomerService customerService;
    private final CompanyService companyService;
    private final ObituaryService obituaryService;
    private final MessageService messageService;
    private final DeathCertificateService deathCertificateService;
    private final CustomerRepository customerRepository;
    private final ObituaryRepository obituaryRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, Hash hash,
            CustomerService customerService, CompanyService companyService,
            ObituaryService obituaryService, MessageService messageService, DeathCertificateService deathCertificateService,
            CustomerRepository customerRepository, ObituaryRepository obituaryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.hash = hash;
        this.customerService = customerService;
        this.companyService = companyService;
        this.obituaryService = obituaryService;
        this.messageService = messageService;
        this.deathCertificateService = deathCertificateService;
        this.customerRepository = customerRepository;
        this.obituaryRepository = obituaryRepository;
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
        userRepository.delete(user);
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
        Customer customer = customerService.findById(id);
        Company company = companyService.findById(id);

        if(customer != null){
            customer.setName("Anónimo");
            customer.setEmail("anonimo"+hash.hash(customer.getEmail())+".com");
            customer.setTelephone("000000000");
            customer.setPassword("anonimo"+hash.hash(customer.getPassword()));
            customerRepository.save(customer);
            List<Obituary> obituaries = obituaryService.getAllObituariesByCustomer(id);
            for (Obituary o : obituaries){
                o.setName("Anónimo");
                o.setFarewellMessage("Anónimo");
                o.setFarewellPhrase("Anonimo");
                o.setCustomImageUrl("Anonimo");
                obituaryRepository.save(o);
            }


        }
        else if(company != null){
            company.setName("Anónimo");
            company.setEmail("anonimo"+hash.hash(company.getEmail())+".com");
            company.setTelephone("000000000");
            company.setPassword("anonimo"+hash.hash(company.getPassword()));
            company.setAddress("Anónimo");
            company.setCity("Anónimo");
            company.setNif("00000000A");
            company.setZipCode("00000");
            company.setDescription("Anónimo");
            company.setImageUrl("Anonimo");
        }
        else{
            throw new ResourceNotFound("User", "ID", id);
        }


    }

}
