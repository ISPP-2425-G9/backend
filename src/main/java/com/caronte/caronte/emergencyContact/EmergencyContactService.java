package com.caronte.caronte.emergencyContact;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.customer.CustomerService;
import com.caronte.caronte.emergencyContact.DTOs.EmergencyContactDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
        checkIfTelephoneIsDuplicate(emergencyContactDTO.telephone(), email);
        checkIfEmailIsDuplicate(emergencyContactDTO.email(), email);
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


    private void checkIfTelephoneIsDuplicate(String telephone, String customerEmail) {
        if (emergencyContactRepository.existsByTelephoneAndCustomer(telephone, customerEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El número de teléfono ya está registrado.");
        }
    }

    private void checkIfEmailIsDuplicate(String email, String customerEmail) {
        if (emergencyContactRepository.existsByEmailAndCustomer(email, customerEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está registrado.");
        }
    }
}
