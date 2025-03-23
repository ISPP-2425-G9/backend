package com.caronte.caronte.company;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    // Obtener compañías con plan PREMIUM
    @GetMapping("/premium")
    public ResponseEntity<List<CompanyDTO>> getPremiumCompanies() {
        return ResponseEntity.ok(companyService.findAllCompaniesPublicInformation());
    }
}
