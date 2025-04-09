package com.caronte.caronte.customer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;
import com.caronte.caronte.util.exceptions.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        // Arrange: Se crean dos clientes dummy
        Customer customer1 = new Customer();
        customer1.setId(1L);
        Customer customer2 = new Customer();
        customer2.setId(2L);
        List<Customer> customers = List.of(customer1, customer2);
        when(customerRepository.findAll()).thenReturn(customers);

        // Act
        List<Customer> result = customerService.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testFindById_Success() {
        // Arrange: Se simula que el repositorio encuentra un cliente con id 1
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Act
        Customer result = customerService.findById(1L);

        // Assert
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindById_NotFound() {
        // Arrange: Se simula que el cliente no existe
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert: Se espera que se lance ResourceNotFound
        assertThrows(ResourceNotFound.class, () -> customerService.findById(1L));
    }

    @Test
    void testFindByDni_Success() {
        // Arrange: Se simula la búsqueda por DNI
        String dni = "ABC123";
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setDni(dni);
        when(customerRepository.findByDni(dni)).thenReturn(Optional.of(customer));

        // Act
        Customer result = customerService.findByDni(dni);

        // Assert
        assertEquals(dni, result.getDni());
    }

    @Test
    void testFindByDni_NotFound() {
        // Arrange: Se simula que no existe ningún cliente con el DNI dado
        String dni = "ABC123";
        when(customerRepository.findByDni(dni)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFound.class, () -> customerService.findByDni(dni));
    }

    @Test
    void testUpdate_Success() {
        // Arrange: Se simula el cliente a actualizar
        Long id = 1L;
        // Usamos spy para poder verificar el método update() en el objeto Customer
        Customer existingCustomer = spy(new Customer());
        existingCustomer.setId(id);

        // Se crea un request de actualización (por ejemplo, se actualiza el nombre)
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
        updateRequest.setFullName("Updated Name");

        // Para el test, el método update() se comporta como un método void
        doNothing().when(existingCustomer).update(updateRequest);

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
        // Se simula que al guardar, el repositorio retorna el mismo objeto actualizado
        when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);

        // Act
        Customer result = customerService.update(id, updateRequest);

        // Assert: Verifica que se llamó al método update() y se guardó el cliente
        verify(existingCustomer, times(1)).update(updateRequest);
        verify(customerRepository, times(1)).save(existingCustomer);
        assertEquals(id, result.getId());
    }

    @Test
    void testUpdate_NotFound() {
        // Arrange: Se simula que no se encuentra el cliente a actualizar
        Long id = 1L;
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert: Se espera que se lance ResourceNotFound
        assertThrows(ResourceNotFound.class, () -> customerService.update(id, updateRequest));
    }
}
