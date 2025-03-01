package com.caronte.caronte.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface LongRepository<T> extends JpaRepository<T, Long> {
    
}
