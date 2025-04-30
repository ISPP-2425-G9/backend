package com.caronte.caronte.imageTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.caronte.caronte.util.exceptions.ResourceNotFound;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ImageTemplateServiceTest {

    @Mock
    private ImageTemplateRepository imageTemplateRepository;

    @InjectMocks
    private ImageTemplateService imageTemplateService;

    @Test
    void testFindById_Success() {
        Long id = 1L;
        ImageTemplate dummy = new ImageTemplate();
        dummy.setId(id);
        dummy.setImageUrl("http://example.com/template.jpg");
        
        when(imageTemplateRepository.findById(id)).thenReturn(Optional.of(dummy));
        
        ImageTemplate result = imageTemplateService.findById(id);
        
        assertEquals(dummy, result);
    }

    @Test
    void testFindById_NotFound() {
        Long id = 1L;
        when(imageTemplateRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> {
            imageTemplateService.findById(id);
        });
        

        assertEquals("404 NOT_FOUND \"Image template not found\"", exception.getMessage());
    }

    @Test
    void testGetAllTemplateUrls() {
        ImageTemplate dummy1 = new ImageTemplate();
        dummy1.setId(1L);
        dummy1.setImageUrl("http://example.com/template1.jpg");
        
        ImageTemplate dummy2 = new ImageTemplate();
        dummy2.setId(2L);
        dummy2.setImageUrl("http://example.com/template2.jpg");
        
        List<ImageTemplate> dummyList = Arrays.asList(dummy1, dummy2);
        when(imageTemplateRepository.findAll()).thenReturn(dummyList);
        
        List<ImageTemplate> resultList = imageTemplateService.getAllTemplateUrls();
        
        assertEquals(dummyList.size(), resultList.size());
        assertEquals(dummyList, resultList);
    }
}
