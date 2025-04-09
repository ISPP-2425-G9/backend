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
        Customer customer1 = new Customer();
        customer1.setId(1L);
        Customer customer2 = new Customer();
        customer2.setId(2L);
        List<Customer> customers = List.of(customer1, customer2);
        when(customerRepository.findAll()).thenReturn(customers);

       
        List<Customer> result = customerService.findAll();

        
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void testFindById_Success() {
        
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

       
        Customer result = customerService.findById(1L);

        
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindById_NotFound() {
       
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

       
        assertThrows(ResourceNotFound.class, () -> customerService.findById(1L));
    }

    @Test
    void testFindByDni_Success() {
       
        String dni = "ABC123";
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setDni(dni);
        when(customerRepository.findByDni(dni)).thenReturn(Optional.of(customer));

   
        Customer result = customerService.findByDni(dni);

       
        assertEquals(dni, result.getDni());
    }

    @Test
    void testFindByDni_NotFound() {
      
        String dni = "ABC123";
        when(customerRepository.findByDni(dni)).thenReturn(Optional.empty());

       
        assertThrows(ResourceNotFound.class, () -> customerService.findByDni(dni));
    }

    @Test
    void testUpdate_Success() {
        Long id = 1L;
       
        Customer existingCustomer = spy(new Customer());
        existingCustomer.setId(id);
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
        updateRequest.setFullName("Updated Name");

        doNothing().when(existingCustomer).update(updateRequest);

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));

        when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);

        
        Customer result = customerService.update(id, updateRequest);

       
        verify(existingCustomer, times(1)).update(updateRequest);
        verify(customerRepository, times(1)).save(existingCustomer);
        assertEquals(id, result.getId());
    }

    @Test
    void testUpdate_NotFound() {

        Long id = 1L;
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> customerService.update(id, updateRequest));
    }
}
