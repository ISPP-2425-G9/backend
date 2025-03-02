package com.caronte.caronte.customer;

import java.lang.classfile.ClassFile.Option;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;


@Service
public class CustomerService {
    private CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
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
        customerToUpdate.setDni(request.getDni());
        customerToUpdate.setName(request.getFullName());
        customerToUpdate.setTelephone(request.getTelephone());
        
        return customerRepository.save(customerToUpdate);
    }


    

}
