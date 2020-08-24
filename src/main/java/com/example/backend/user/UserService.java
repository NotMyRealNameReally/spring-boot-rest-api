package com.example.backend.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.backend.exception.user.InvalidPasswordException;
import com.example.backend.exception.user.InvalidRegistrationTokenException;
import com.example.backend.exception.user.UserAlreadyExistsException;
import com.example.backend.user.form.UserRegistrationForm;
import com.example.backend.user.registrationtoken.RegistrationTokenRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private RegistrationTokenRepository registrationTokenRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository,
                       RegistrationTokenRepository registrationTokenRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.registrationTokenRepository = registrationTokenRepository;
    }

    public void registerUser(UserRegistrationForm userForm) {
        if (registrationTokenRepository.findByValue(userForm.getToken()).isEmpty()) {
            throw new InvalidRegistrationTokenException("Invalid token");
        }
        if (userRepository.findByUsername(userForm.getUsername()).isPresent() ||
                userRepository.findByEmail(userForm.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        String encodedPassword = passwordEncoder.encode(userForm.getPassword());
        ApplicationUser user = new ApplicationUser(userForm.getUsername(), encodedPassword, userForm.getEmail(),
                true, true, true, true, authorities);
        userRepository.save(user);
        registrationTokenRepository.removeByValue(userForm.getToken());
    }

    public void changeUserPassword(String username, String oldPassword, String newPassword) {
        ApplicationUser user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", username)));

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new InvalidPasswordException("Invalid password");
        }
    }

    public List<ApplicationUser> findAllUsers() {
        return userRepository.findAll();
    }
}
