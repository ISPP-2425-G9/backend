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

    /**
     * Ejemplo de URI para probar este endpoint:
     * GET http://localhost:8080/companies/premium?page=1&size=5&sort=name,asc&city=Sevilla&name=Floristería&companyType=FLORIST
     *
     * Parámetros:
     * - page (opcional): número de página (comienza en 0)
     * - size (opcional): cantidad de resultados por página
     * - sort (opcional): campo por el que ordenar, ejemplo: sort=name,asc
     * - city (opcional): filtro por ciudad (busca coincidencias parciales)
     * - name (opcional): filtro por nombre de compañía (busca coincidencias parciales)
     * - companyType (opcional): tipo de compañía (valores: FLORIST, NOTARIES, FUNERAL_HOMES, LAW_FIRMS, OTHER)
     */

    @GetMapping("/premium")
    public Page<CompanyDTO> getPremiumCompanies(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) CompanyType companyType,
            Pageable pageable
    ) {
        return companyService.findAllCompaniesPublicInformation(city, name, companyType, pageable);
    }

}
