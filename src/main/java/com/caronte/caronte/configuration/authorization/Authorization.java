package com.caronte.caronte.configuration.authorization;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Authorization {
    COMPANY,
    COMPANY_FREE,
    COMPANY_PREMIUM,
    CUSTOMER,
    CUSTOMER_FREE,
    CUSTOMER_PREMIUM,
    ADMIN;

    public SimpleGrantedAuthority getAuthority(){
        return new SimpleGrantedAuthority(this.name());
    }
}
