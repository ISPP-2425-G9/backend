package com.caronte.caronte.auth;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.RegisterRequestCompany;
import com.caronte.caronte.auth.payload.response.RegisterRequestCustomer;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyRepository;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserRepository;
import com.caronte.caronte.util.ErrorHandler;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AuthService(UserRepository userRepository, CustomerRepository customerRepository, 
            CompanyRepository companyRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
	}

    @Transactional(readOnly = true)
    public String getNameById(Long userId) {
        return userRepository.findById(userId)
                             .map(User::getName)
                             .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public Customer validateAndBuildCustomer(RegisterRequestCustomer registerRequest, ErrorHandler errors) {
        if (Objects.nonNull(registerRequest.getPassword1()) && !Objects.equals(registerRequest.getPassword1(), registerRequest.getPassword2()))
            errors.addError("password", "Las contraseñas no coinciden");
    
        if (userRepository.existsByEmail(registerRequest.getEmail())) 
            errors.addError("email", "El email ya está en uso");
        
        if (customerRepository.existsByDni(registerRequest.getDni()))
            errors.addError("dni", "El dni ya está en uso");

        return registerRequest.parse(passwordEncoder);
    }

    @Transactional(readOnly = true)
    public Company validateAndBuildCompany(RegisterRequestCompany registerRequest, ErrorHandler errors) {        
        if (Objects.nonNull(registerRequest.getPassword1()) && !Objects.equals(registerRequest.getPassword1(), registerRequest.getPassword2()))
            errors.addError("password", "Las contraseñas no coinciden");
    
        if (userRepository.existsByEmail(registerRequest.getEmail()))
            errors.addError("email", "El email ya está en uso");

        if (companyRepository.existsByNif(registerRequest.getNif()))
            errors.addError("nif", "El NIF ya está en uso");

        return registerRequest.parse(passwordEncoder);
    }
    
    @Transactional
    public void save(User user){
        userRepository.save(user);
    }

}
