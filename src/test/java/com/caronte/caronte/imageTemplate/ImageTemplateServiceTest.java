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
        // Arrange: Se crea una instancia dummy de ImageTemplate
        Long id = 1L;
        ImageTemplate dummy = new ImageTemplate();
        dummy.setId(id);
        dummy.setImageUrl("http://example.com/template.jpg");
        
        // Simula que el repositorio retorna la entidad encontrada
        when(imageTemplateRepository.findById(id)).thenReturn(Optional.of(dummy));
        
        // Act: Se invoca el método findById del servicio
        ImageTemplate result = imageTemplateService.findById(id);
        
        // Assert: Se comprueba que la entidad retornada es igual a la esperada
        assertEquals(dummy, result);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange: Se configura el repositorio para que no encuentre la entidad
        Long id = 1L;
        when(imageTemplateRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert: Se espera que se lance la excepción ResourceNotFound
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> {
            imageTemplateService.findById(id);
        });
        
        // Se verifica que el mensaje de error sea el esperado
        assertEquals("404 NOT_FOUND \"Image template not found\"", exception.getMessage());
    }

    @Test
    void testGetAllTemplateUrls() {
        // Arrange: Se crean dos instancias dummy de ImageTemplate
        ImageTemplate dummy1 = new ImageTemplate();
        dummy1.setId(1L);
        dummy1.setImageUrl("http://example.com/template1.jpg");
        
        ImageTemplate dummy2 = new ImageTemplate();
        dummy2.setId(2L);
        dummy2.setImageUrl("http://example.com/template2.jpg");
        
        List<ImageTemplate> dummyList = Arrays.asList(dummy1, dummy2);
        // Se simula que el repositorio retorna la lista de plantillas
        when(imageTemplateRepository.findAll()).thenReturn(dummyList);
        
        // Act: Se invoca el método getAllTemplateUrls del servicio
        List<ImageTemplate> resultList = imageTemplateService.getAllTemplateUrls();
        
        // Assert: Se verifica que la lista retornada coincida en tamaño y contenido con la esperada
        assertEquals(dummyList.size(), resultList.size());
        assertEquals(dummyList, resultList);
    }
}
