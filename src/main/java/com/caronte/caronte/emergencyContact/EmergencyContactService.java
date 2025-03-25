package com.caronte.caronte.emergencyContact;

import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.customer.CustomerService;
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

    public List<EmergencyContact> findAll(String email) {
        return emergencyContactRepository.findAllByCustomerEmail(email);
    }



}
