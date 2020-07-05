package com.example.backend.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.backend.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                             .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", username)));
    }

    public void registerUser(UserForm userForm) {
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
    }

    public List<ApplicationUser> findAllUsers() {
        return userRepository.findAll();
    }
}
