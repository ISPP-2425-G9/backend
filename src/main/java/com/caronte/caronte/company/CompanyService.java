package com.caronte.caronte.company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Iterable<Company> findAll() {
        return companyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Company findById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));
        return company;
    }

    @Transactional
    public Company update(Long id, CompanyUpdateRequest request) {
        Company companyToUpdate = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));
        companyToUpdate.setName(request.getName());
        companyToUpdate.setAddress(request.getAddress());
        companyToUpdate.setCity(request.getCity());
        companyToUpdate.setZipCode(request.getZipCode());
        companyToUpdate.setEmail(request.getEmail());
        companyToUpdate.setPassword(passwordEncoder.encode(request.getPassword()));
        companyToUpdate.setTelephone(request.getTelephone());
        companyToUpdate.setImageUrl(request.getImageUrl());
        companyToUpdate.setDescription(request.getDescription());
        if (!request.getPassword().equals(companyToUpdate.getPassword())) {
            companyToUpdate.setPassword(request.getPassword());
        }
        return companyRepository.save(companyToUpdate);
    }

    @Transactional
    public Page<CompanyDTO> findAllCompaniesPublicInformation(String city, String name, CompanyType companyType, Pageable pageable) {
        Page<Company> companies = companyRepository.findPremiumCompaniesFiltered(city, name, companyType, pageable);
        return companies.map(company -> new CompanyDTO(
                company.getName(),
                company.getEmail(),
                company.getTelephone(),
                company.getAddress(),
                company.getCity(),
                company.getZipCode(),
                company.getImageUrl(),
                company.getDescription(),
                company.getNif(),
                company.getCompanyType()
        ));
    }

}
