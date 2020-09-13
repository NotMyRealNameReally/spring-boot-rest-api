package com.example.backend.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.backend.exception.AppException;
import com.example.backend.user.form.UserRegistrationForm;
import com.example.backend.user.registrationtoken.RegistrationTokenRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.backend.exception.ExceptionType.INVALID_PASSWORD;
import static com.example.backend.exception.ExceptionType.INVALID_REGISTRATION_TOKEN;
import static com.example.backend.exception.ExceptionType.USER_ALREADY_EXISTS;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RegistrationTokenRepository registrationTokenRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository,
                       RegistrationTokenRepository registrationTokenRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.registrationTokenRepository = registrationTokenRepository;
    }

    public ApplicationUser registerUser(UserRegistrationForm userForm) {
        registrationTokenRepository
                .findByValue(userForm.getToken())
                .orElseThrow(() -> new AppException("Invalid token", INVALID_REGISTRATION_TOKEN));
        if (userExists(userForm.getUsername(), userForm.getEmail())) {
            throw new AppException("User already exists", USER_ALREADY_EXISTS);
        }
        registrationTokenRepository.removeByValue(userForm.getToken());
        return userRepository.save(createUserFromForm(userForm));
    }

    public void changeUserPassword(String username, String oldPassword, String newPassword) {
        ApplicationUser user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", username)));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AppException("Invalid password", INVALID_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public List<ApplicationUser> findAllUsers() {
        return userRepository.findAll();
    }

    private ApplicationUser createUserFromForm(UserRegistrationForm form) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        String encodedPassword = passwordEncoder.encode(form.getPassword());

        return new ApplicationUser(form.getUsername(), encodedPassword, form.getEmail(), authorities, false
        );
    }

    private boolean userExists(String username, String email) {
        return userRepository.findByUsername(username).isPresent() ||
                userRepository.findByEmail(email).isPresent();
    }
}
