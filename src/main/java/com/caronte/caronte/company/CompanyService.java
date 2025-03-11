package com.caronte.caronte.company;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.CompanyUpdateRequest;
import com.caronte.caronte.plan.PlanType;



@Service
public class CompanyService { 

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public CompanyService(CompanyRepository companyRepository, PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
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
        companyToUpdate.setPassword(this.passwordEncoder.encode(request.getPassword()));
        return companyRepository.save(companyToUpdate);
    }

    @Transactional
    public List<CompanyDTO> findAllCompaniesPublicInformation(){
        List<Company> companiesWithPlan = companyRepository.findByPlan_PlanType(PlanType.PREMIUM);
        return companiesWithPlan.stream().map(company -> new CompanyDTO(
            company.getName(), 
            company.getEmail(), 
            company.getTelephone(), 
            company.getAddress(), 
            company.getCity(), 
            company.getZipCode(), 
            company.getImageUrl(), 
            company.getDescription(),
            company.getNif()))
            .toList();
    }

    

}
