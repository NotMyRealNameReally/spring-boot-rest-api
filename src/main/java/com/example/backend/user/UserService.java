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
                .orElseThrow(() -> new InvalidRegistrationTokenException("Invalid token"));
        if (userExists(userForm.getUsername(), userForm.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }
        registrationTokenRepository.removeByValue(userForm.getToken());
        return userRepository.save(createUserFromForm(userForm));
    }

    public void changeUserPassword(String username, String oldPassword, String newPassword) {
        ApplicationUser user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", username)));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
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
