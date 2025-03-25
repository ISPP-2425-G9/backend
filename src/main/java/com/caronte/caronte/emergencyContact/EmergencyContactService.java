package com.caronte.caronte.emergencyContact;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.customer.CustomerService;
import com.caronte.caronte.emergencyContact.DTOs.EmergencyContactDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @Transactional
    public EmergencyContactDTO save(EmergencyContactDTO emergencyContactDTO, String email) {
        System.out.println("emergencyContactDTO: " + emergencyContactDTO);
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }
        EmergencyContact emergencyContact = new EmergencyContact(emergencyContactDTO, customer.get());
        EmergencyContact emergencyContactCreated = emergencyContactRepository.save(emergencyContact);
        return new EmergencyContactDTO(emergencyContactCreated.getName(),
                emergencyContactCreated.getTelephone(),
                emergencyContactCreated.getEmail());
    }



}
