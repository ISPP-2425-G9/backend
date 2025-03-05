package com.caronte.caronte.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.CompanyUpdateRequest;



@Service
public class CompanyService { 

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
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
        companyToUpdate.setTelephone(request.getTelephone());
        companyToUpdate.setImageUrl(request.getImageUrl());
        companyToUpdate.setDescription(request.getDescription());
    
        return companyRepository.save(companyToUpdate);
    }

    

}
