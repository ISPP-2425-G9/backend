package com.caronte.caronte.company;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.CompanyUpdateRequest;
import com.caronte.caronte.plan.PlanType;
import com.caronte.caronte.util.exceptions.ResourceNotFound;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Company findById(Long id) {
        Company company = companyRepository.findById(id).orElseThrow(() -> ResourceNotFound.of("Company"));
        return company;
    }

    @Transactional
    public Company update(Long id, CompanyUpdateRequest request) {
        Company companyToUpdate = companyRepository.findById(id).orElseThrow(() -> ResourceNotFound.of("Company"));
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

    @Transactional
    public List<CompanyDTO> findAllCompaniesPublicInformation(){
        List<Company> companiesWithPlan = companyRepository.findByPlan_PlanType(PlanType.PREMIUM);
        return companiesWithPlan.stream().map(CompanyDTO::parse).toList();
    }

}
