package com.caronte.caronte._User;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.caronte.caronte.util.LongRepository;


@Repository
public interface UserRepository extends LongRepository<User> {
    Optional<User> findByEmail(String email);
}
