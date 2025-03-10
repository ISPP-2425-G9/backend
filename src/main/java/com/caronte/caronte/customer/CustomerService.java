package com.caronte.caronte.customer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;


@Service
public class CustomerService {
    
    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
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
        customerToUpdate.setPassword(request.getPassword());
        
        return customerRepository.save(customerToUpdate);
    }


    

}
