package com.example.backend.user;

import java.util.Collections;
import java.util.Optional;

import com.example.backend.exception.user.InvalidRegistrationTokenException;
import com.example.backend.exception.user.UserAlreadyExistsException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

public class CustomUserDetailsServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RegistrationTokenRepository registrationTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Captor
    private ArgumentCaptor<ApplicationUser> userCaptor;
    private CustomUserDetailsService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomUserDetailsService(userRepository, registrationTokenRepository, passwordEncoder);
    }

    @Test
    void should_Register_User() {
        UserForm form = new UserForm("user", "user@test.com", "password", "token");
        given(registrationTokenRepository.findByValue(form.getToken()))
                .willReturn(Optional.of(new RegistrationToken()));
        given(userRepository.findByUsername(form.getUsername()))
                .willReturn(Optional.empty());
        given(userRepository.findByEmail(form.getEmail()))
                .willReturn(Optional.empty());
        given(passwordEncoder.encode(form.getPassword()))
                .willReturn("encoded");

        underTest.registerUser(form);

        then(userRepository).should().save(userCaptor.capture());
        then(registrationTokenRepository).should().removeByValue("token");
        ApplicationUser user = userCaptor.getValue();
        assertThat(user.getUsername()).isEqualTo(form.getUsername());
        assertThat(user.getEmail()).isEqualTo(form.getEmail());
        assertThat(user.getPassword()).isEqualTo("encoded");
        assertThat(user.getAuthorities()).containsOnly(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Test
    void should_Not_Register_User_And_Throw_InvalidRegistrationTokenException_When_Token_Invalid() {
        UserForm form = new UserForm("user", "user@test.com", "password", "token");
        ApplicationUser user = new ApplicationUser("user", "password", "user@test.com",
                Collections.emptyList());
        given(registrationTokenRepository.findByValue(form.getToken()))
                .willReturn(Optional.empty());
        given(userRepository.findByUsername(form.getUsername()))
                .willReturn(Optional.of(user));
        given(userRepository.findByEmail(form.getEmail()))
                .willReturn(Optional.of(user));

        Assertions.assertThatThrownBy(() -> underTest.registerUser(form))
                  .isInstanceOf(InvalidRegistrationTokenException.class);

        then(userRepository).shouldHaveNoInteractions();
    }

    @Test
    void should_Not_Register_User_And_Throw_UserAlreadyExistsException_When_Username_Taken() {
        UserForm form = new UserForm("user", "user@test.com", "password", "token");
        ApplicationUser user = new ApplicationUser("user", "password", "user@test.com",
                Collections.emptyList());
        given(registrationTokenRepository.findByValue(form.getToken()))
                .willReturn(Optional.of(new RegistrationToken()));
        given(userRepository.findByUsername(form.getUsername()))
                .willReturn(Optional.of(user));
        given(userRepository.findByEmail(form.getEmail()))
                .willReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> underTest.registerUser(form))
                  .isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should(never()).save(any(ApplicationUser.class));
    }

    @Test
    void should_Not_Register_User_And_Throw_UserAlreadyExistsException_When_Email_Taken() {
        UserForm form = new UserForm("user", "user@test.com", "password", "token");
        ApplicationUser user = new ApplicationUser("user", "password", "user@test.com",
                Collections.emptyList());
        given(registrationTokenRepository.findByValue(form.getToken()))
                .willReturn(Optional.of(new RegistrationToken()));
        given(userRepository.findByUsername(form.getUsername()))
                .willReturn(Optional.empty());
        given(userRepository.findByEmail(form.getEmail()))
                .willReturn(Optional.of(user));

        Assertions.assertThatThrownBy(() -> underTest.registerUser(form))
                  .isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should(never()).save(any(ApplicationUser.class));
    }
}
