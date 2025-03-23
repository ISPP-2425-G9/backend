package com.caronte.caronte.customer;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;


@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Iterable<Customer> findAll(){
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id){
        return customerRepository.findById(id)
                                 .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    @Transactional
    public Customer update(Long id, CustomerUpdateRequest request) {
        Customer customerToUpdate = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        customerToUpdate.setEmail(request.getEmail());
        customerToUpdate.setName(request.getFullName());
        customerToUpdate.setTelephone(request.getTelephone());
        return customerRepository.save(customerToUpdate);
    }
}
