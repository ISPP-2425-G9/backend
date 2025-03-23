package com.caronte.caronte.customer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;
import com.caronte.caronte.user.UserRepository;
import com.caronte.caronte.user.UserService;


@Service
public class CustomerService {

    private final UserRepository userRepository;

    private final UserService userService;

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, UserService userService, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userService = userService;
        this.userRepository = userRepository;
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

    @Transactional(readOnly = true)
    public Customer findByDni(String dni){
        return customerRepository.findByDni(dni)
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
