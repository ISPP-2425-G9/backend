package com.caronte.caronte.company;

import com.caronte.caronte.plan.PlanType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    // Obtener compañías con plan PREMIUM
    @GetMapping("/premium")
    public List<CompanyDTO> getPremiumCompanies() {
        return companyService.findAllCompaniesPublicInformation();
    }
}
