package com.caronte.caronte.customer;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;


@Service
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Iterable<Customer> findAll(){
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id){
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        return customer;
    }

    @Transactional
    public Customer update(Long id, CustomerUpdateRequest request) {
        Customer customerToUpdate = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        
        customerToUpdate.setEmail(request.getEmail());
        customerToUpdate.setName(request.getFullName());
        customerToUpdate.setTelephone(request.getTelephone());
        if (!request.getPassword().equals(customerToUpdate.getPassword())) {
            customerToUpdate.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return customerRepository.save(customerToUpdate);
    }
}
