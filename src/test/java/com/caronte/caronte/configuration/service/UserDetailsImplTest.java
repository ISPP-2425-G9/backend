package com.caronte.caronte.configuration.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import com.caronte.caronte.admin.Admin;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.configuration.authorization.Authorization;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.plan.Plan;
import com.caronte.caronte.plan.PlanType;
import com.caronte.caronte.user.User;

public class UserDetailsImplTest {

    @Test
    void testBuildAdmin() {
        Admin admin = mock(Admin.class);
        when(admin.getId()).thenReturn(1L);
        when(admin.getEmail()).thenReturn("admin@example.com");
        when(admin.getPassword()).thenReturn("adminpass");

        UserDetailsImpl userDetails = UserDetailsImpl.build(admin);
        assertEquals(1L, userDetails.getId());
        assertEquals("admin@example.com", userDetails.getUsername());
        assertEquals("adminpass", userDetails.getPassword());

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ADMIN")),
                "Se esperaba autoridad " + Authorization.ADMIN.getAuthority());
        assertTrue(userDetails.isAdmin(), "El usuario debe ser admin");
    }

    @Test
    void testBuildCustomerFree() {
        Customer customer = mock(Customer.class);
        Plan freePlan = Plan.newPlanFree();
        when(customer.getPlan()).thenReturn(freePlan);
        when(customer.getId()).thenReturn(2L);
        when(customer.getEmail()).thenReturn("customer@example.com");
        when(customer.getPassword()).thenReturn("custpass");

      
        UserDetailsImpl userDetails = UserDetailsImpl.build(customer);

        
        assertEquals(2L, userDetails.getId());
        assertEquals("customer@example.com", userDetails.getUsername());
        assertEquals("custpass", userDetails.getPassword());
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("CUSTOMER")),
                "Falta autoridad " + Authorization.CUSTOMER.getAuthority());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("CUSTOMER")),
                "Falta autoridad " + Authorization.CUSTOMER_FREE.getAuthority());
    }

    @Test
    void testBuildCustomerPremium() {
       
        Customer customer = mock(Customer.class);
        Plan premiumPlan = mock(Plan.class);
        when(premiumPlan.getPlanType()).thenReturn(PlanType.PREMIUM);
        when(customer.getPlan()).thenReturn(premiumPlan);
        when(customer.getId()).thenReturn(3L);
        when(customer.getEmail()).thenReturn("customer2@example.com");
        when(customer.getPassword()).thenReturn("custpass2");

       
        UserDetailsImpl userDetails = UserDetailsImpl.build(customer);

       
        assertEquals(3L, userDetails.getId());
        assertEquals("customer2@example.com", userDetails.getUsername());
        assertEquals("custpass2", userDetails.getPassword());
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("CUSTOMER")),
                "Falta autoridad " + Authorization.CUSTOMER.getAuthority());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("CUSTOMER_PREMIUM")),
                "Falta autoridad " + Authorization.CUSTOMER_PREMIUM.getAuthority());
    }

    @Test
    void testBuildCompanyFree() {
       
        Company company = mock(Company.class);
        Plan freePlan = Plan.newPlanFree();
        when(company.getPlan()).thenReturn(freePlan);
        when(company.getId()).thenReturn(4L);
        when(company.getEmail()).thenReturn("company@example.com");
        when(company.getPassword()).thenReturn("comppass");

        
        UserDetailsImpl userDetails = UserDetailsImpl.build(company);

        
        assertEquals(4L, userDetails.getId());
        assertEquals("company@example.com", userDetails.getUsername());
        assertEquals("comppass", userDetails.getPassword());
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("COMPANY")),
                "Falta autoridad " + Authorization.COMPANY.getAuthority());
        assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("COMPANY_FREE")),
                "Falta autoridad " + Authorization.COMPANY_FREE.getAuthority());
    }

    @Test
    void testBuildCompanyPremium() {
        Company company = mock(Company.class);
        Plan premiumPlan = mock(Plan.class);
        when(premiumPlan.getPlanType()).thenReturn(PlanType.PREMIUM);
        when(company.getPlan()).thenReturn(premiumPlan);
        when(company.getId()).thenReturn(5L);
        when(company.getEmail()).thenReturn("company2@example.com");
        when(company.getPassword()).thenReturn("comppass2");

        
        UserDetailsImpl userDetails = UserDetailsImpl.build(company);

        assertEquals(5L, userDetails.getId());
        assertEquals("company2@example.com", userDetails.getUsername());
        assertEquals("comppass2", userDetails.getPassword());
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(2, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("COMPANY")),
                "Falta autoridad " + Authorization.COMPANY.getAuthority());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("COMPANY_PREMIUM")),
                "Falta autoridad " + Authorization.COMPANY_PREMIUM.getAuthority());
    }

    @Test
    void testBuildInvalidUser() {
        
        User dummyUser = new User() {}; 
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> UserDetailsImpl.build(dummyUser));
        assertEquals("User isn't instance of Admin, Customer or Company", exception.getMessage());
    }

    @Test
    void testEqualsAndHashCode() {
       
        Admin admin1 = mock(Admin.class);
        when(admin1.getId()).thenReturn(10L);
        when(admin1.getEmail()).thenReturn("admin1@example.com");
        when(admin1.getPassword()).thenReturn("pass1");

        Admin admin2 = mock(Admin.class);
        when(admin2.getId()).thenReturn(10L);
        when(admin2.getEmail()).thenReturn("admin2@example.com");
        when(admin2.getPassword()).thenReturn("pass2");

        UserDetailsImpl details1 = UserDetailsImpl.build(admin1);
        UserDetailsImpl details2 = UserDetailsImpl.build(admin2);

        assertEquals(details1, details2, "Dos usuarios con el mismo ID deben ser iguales");
        assertEquals(details1.hashCode(), details2.hashCode(), "Los hashCode deben coincidir");
    }
}
