package com.caronte.caronte.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("/premium")
    public Page<CompanyDTO> getPremiumCompanies(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name,
            Pageable pageable
    ) {
        return companyService.findAllCompaniesPublicInformation(city, name, pageable);
    }

}
