package com.caronte.caronte.emergencyContact;

import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.customer.CustomerService;
import com.caronte.caronte.emergencyContact.DTOs.EmergencyContactDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmergencyContactService {

    private EmergencyContactRepository emergencyContactRepository;

    private CustomerRepository customerRepository;

    public EmergencyContactService (EmergencyContactRepository emergencyContactRepository, CustomerRepository customerRepository) {
        this.emergencyContactRepository = emergencyContactRepository;
        this.customerRepository = customerRepository;
    }

    public List<EmergencyContactDTO> findAll(String email) {
        List<EmergencyContact> emergencyContacts =  emergencyContactRepository.findAllByCustomerEmail(email);
        return emergencyContacts.stream()
                .map(contact -> new EmergencyContactDTO(contact.getName(), contact.getTelephone(), contact.getEmail()))
                .toList();
    }

    public EmergencyContact save(EmergencyContact emergencyContact) {
        return emergencyContactRepository.save(emergencyContact);
    }



}
