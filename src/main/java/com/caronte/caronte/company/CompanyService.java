package com.caronte.caronte.company;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.CompanyUpdateRequest;



@Service
public class CompanyService { 

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public CompanyService(CompanyRepository companyRepository, PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Iterable<Company> findAll(){
        return companyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Company findById(Long id){   
        Company company = companyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Company not found"));
        return company;
    }

    @Transactional
    public Company update(Long id, CompanyUpdateRequest request) {
        Company companyToUpdate = companyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Company not found"));
        companyToUpdate.setName(request.getName());
        companyToUpdate.setAddress(request.getAddress());
        companyToUpdate.setCity(request.getCity());
        companyToUpdate.setZipCode(request.getZipCode());
        companyToUpdate.setEmail(request.getEmail());
        companyToUpdate.setPassword(passwordEncoder.encode(request.getPassword()));
        companyToUpdate.setTelephone(request.getTelephone());
        companyToUpdate.setImageUrl(request.getImageUrl());
        companyToUpdate.setDescription(request.getDescription());
        if(passwordEncoder.matches(request.getPassword(), companyToUpdate.getPassword())) {
            companyToUpdate.setPassword(companyToUpdate.getPassword());
        } else {
            companyToUpdate.setPassword(this.passwordEncoder.encode(request.getPassword()));
        }
        return companyRepository.save(companyToUpdate);
    }

    

}
