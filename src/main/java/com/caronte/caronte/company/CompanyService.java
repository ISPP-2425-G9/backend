package com.caronte.caronte.company;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.auth.payload.response.CompanyUpdateRequest;
import com.caronte.caronte.company.DTOs.CompanyDTO;
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
        companyToUpdate.update(request);
        return companyRepository.save(companyToUpdate);
    }

    @Transactional
    public Page<CompanyDTO> findAllCompaniesPublicInformation(String city, String name, CompanyType companyType, Pageable pageable) {
        Page<Company> companies = companyRepository.findPremiumCompaniesFiltered(city, name, companyType, pageable);
        return companies.map(CompanyDTO::parse);
    }

}
