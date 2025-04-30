package com.caronte.caronte.company;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class CompanyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private CompanyController companyController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(companyController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }


    // TODO
    @Test
    public void getPremiumCompanies_withValidParams_returnsCompanies() throws Exception {
        
    }
    // TODO
    @Test
    public void getPremiumCompanies_withoutParams_returnsAllCompanies() throws Exception {
    }


    @Test
    public void getPremiumCompanies_withInvalidCompanyType_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/companies/premium")
                        .param("companyType", "INVALID_TYPE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getCompanyTypes_returnsAllCompanyTypes() throws Exception {
        mockMvc.perform(get("/api/companies/companiesTypes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$", hasItems(
                        CompanyType.FLORISTERIA.toString(),
                        CompanyType.NOTARIA.toString(),
                        CompanyType.FUNERARIA.toString(),
                        CompanyType.DESPACHO_DE_ABOGADOS.toString(),
                        CompanyType.OTRO.toString()
                )));
    }

}
