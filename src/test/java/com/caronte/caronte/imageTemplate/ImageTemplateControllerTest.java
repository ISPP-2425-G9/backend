package com.caronte.caronte.imageTemplate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ImageTemplateControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ImageTemplateService imageTemplateService;

    @InjectMocks
    private ImageTemplateController imageTemplateController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(imageTemplateController)
                .build();
    }

    @Test
    void testGetAllTemplateUrls() throws Exception {
        ImageTemplate template1 = new ImageTemplate();
        template1.setId(1L);
        template1.setImageUrl("http://example.com/template1.jpg");

        ImageTemplate template2 = new ImageTemplate();
        template2.setId(2L);
        template2.setImageUrl("http://example.com/template2.jpg");

        List<ImageTemplate> templates = List.of(template1, template2);

        when(imageTemplateService.getAllTemplateUrls()).thenReturn(templates);

        mockMvc.perform(get("/api/templates/urls")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].imageUrl", is("http://example.com/template1.jpg")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].imageUrl", is("http://example.com/template2.jpg")));
    }
}
