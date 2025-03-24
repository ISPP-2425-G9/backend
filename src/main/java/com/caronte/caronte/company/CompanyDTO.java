package com.caronte.caronte.company;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompanyDTO {
    private String name;
    private String email;
    private String telephone;
    private String address;
    private String city;
    private String zipCode;
    private String imageUrl;
    private String description;
    private String nif;
    private CompanyType companyType;

    public static CompanyDTO parse(Company company) {
        return new CompanyDTO(
            company.getName(), 
            company.getEmail(), 
            company.getTelephone(), 
            company.getAddress(), 
            company.getCity(), 
            company.getZipCode(), 
            company.getImageUrl(), 
            company.getDescription(),
            company.getNif(),
            company.getCompanyType());
    }

}
