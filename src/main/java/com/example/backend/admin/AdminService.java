package com.example.backend.admin;

import com.example.backend.user.registrationtoken.RegistrationToken;
import com.example.backend.user.registrationtoken.RegistrationTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final RegistrationTokenRepository registrationTokenRepository;

    public AdminService(RegistrationTokenRepository registrationTokenRepository) {
        this.registrationTokenRepository = registrationTokenRepository;
    }

    public String generateRegistrationToken() {
        RegistrationToken token = new RegistrationToken();
        registrationTokenRepository.save(token);
        return token.getValue();
    }
}
