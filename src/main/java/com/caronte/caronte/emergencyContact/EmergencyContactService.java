package com.caronte.caronte.emergencyContact;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.emergencyContact.DTOs.EmergencyContactDTO;
import com.caronte.caronte.util.exceptions.ResourceNotFound;
import com.caronte.caronte.util.exceptions.ResponseThrow;

@Service
public class EmergencyContactService {

    private final EmergencyContactRepository emergencyContactRepository;
    private final CustomerRepository customerRepository;

    public EmergencyContactService (EmergencyContactRepository emergencyContactRepository, CustomerRepository customerRepository) {
        this.emergencyContactRepository = emergencyContactRepository;
        this.customerRepository = customerRepository;
    }

    public List<EmergencyContactDTO> findAll(String email) {
        List<EmergencyContact> emergencyContacts =  emergencyContactRepository.findAllByCustomerEmail(email);
        return emergencyContacts.stream()
                .map(contact -> EmergencyContactDTO.parse(contact))
                .collect(Collectors.toList());
    }

    @Transactional
    public EmergencyContactDTO save(EmergencyContactDTO emergencyContactDTO, String email) {
        checkIfTelephoneIsDuplicate(emergencyContactDTO.telephone(), email);
        checkIfEmailIsDuplicate(emergencyContactDTO.email(), email);
        
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> ResourceNotFound.of("Customer"));

        EmergencyContact emergencyContact = new EmergencyContact(emergencyContactDTO, customer);
        EmergencyContact emergencyContactCreated = emergencyContactRepository.save(emergencyContact);
        return EmergencyContactDTO.parse(emergencyContactCreated);
    }

    @Transactional
    public EmergencyContactDTO update(EmergencyContactDTO emergencyContactDTO, Long id, String email){
        EmergencyContact emergencyContact = emergencyContactRepository.findById(id).orElseThrow(() -> ResourceNotFound.of("Emergency Contact"));

        ResponseThrow.checkOrForbidden(emergencyContact.hasCustomerEmail(email));

        if (!emergencyContact.hasTelephone(emergencyContactDTO.telephone())) {
            checkIfTelephoneIsDuplicate(emergencyContactDTO.telephone(), email);
        }
        if (!emergencyContact.hasEmail(emergencyContactDTO.email())) {
            checkIfEmailIsDuplicate(emergencyContactDTO.email(), email);
        }

        emergencyContact.update(emergencyContactDTO);
        EmergencyContact updatedContact = emergencyContactRepository.save(emergencyContact);
        return EmergencyContactDTO.parse(updatedContact);
    }

    @Transactional
    public void delete(Long id, String email) {
        EmergencyContact emergencyContact = emergencyContactRepository.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("Emergency Contact"));
        ResponseThrow.checkOrForbidden(emergencyContact.hasCustomerEmail(email), "No tienes permisos para eliminar este contacto de emergencia");
        emergencyContactRepository.deleteById(id);
    }


    private void checkIfTelephoneIsDuplicate(String telephone, String customerEmail) {
        if (emergencyContactRepository.existsByTelephoneAndCustomer(telephone, customerEmail))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El número de teléfono ya está registrado.");
    }

    private void checkIfEmailIsDuplicate(String email, String customerEmail) {
        if (emergencyContactRepository.existsByEmailAndCustomer(email, customerEmail))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está registrado.");
    }
}
