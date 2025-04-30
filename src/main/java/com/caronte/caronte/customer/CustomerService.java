package com.caronte.caronte.customer;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;
import com.caronte.caronte.util.exceptions.ResourceNotFound;


@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public List<Customer> findAll(){
        return customerRepository.findAll();
    }

    @Transactional
    public List<Customer> findAllWithOutAnonymus(){
        return customerRepository.findByDniNot("anonimo");
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id){
        return customerRepository.findById(id).orElseThrow(() -> ResourceNotFound.of("Customer"));
    }

    @Transactional(readOnly = true)
    public Customer findByDni(String dni){
        return customerRepository.findByDni(dni).orElseThrow(() -> ResourceNotFound.of("Customer"));
    }

    @Transactional
    public Customer update(Long id, CustomerUpdateRequest request) {
        Customer customerToUpdate = customerRepository.findById(id).orElseThrow(() -> ResourceNotFound.of("Customer"));
        customerToUpdate.update(request);
        return customerRepository.save(customerToUpdate);
    }
}
