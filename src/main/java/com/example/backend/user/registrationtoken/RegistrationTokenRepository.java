package com.example.backend.user.registrationtoken;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RegistrationTokenRepository extends MongoRepository<RegistrationToken, String> {
    Optional<RegistrationToken> findByValue(String value);

    void removeByValue(String value);
}
