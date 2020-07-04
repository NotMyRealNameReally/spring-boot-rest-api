package com.example.backend.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<ApplicationUser, String> {

    public Optional<ApplicationUser> findByUsername(String username);
}
