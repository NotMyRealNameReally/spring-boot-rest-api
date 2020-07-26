package com.example.backend.user;

import java.util.Collections;
import java.util.Optional;

import com.example.backend.exception.user.InvalidPasswordException;
import com.example.backend.exception.user.InvalidRegistrationTokenException;
import com.example.backend.exception.user.UserAlreadyExistsException;
import com.example.backend.user.form.UserRegistrationForm;
import com.example.backend.user.registrationtoken.RegistrationToken;
import com.example.backend.user.registrationtoken.RegistrationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        UserRegistrationForm form = new UserRegistrationForm("user", "user@test.com", "password", "token");
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
        UserRegistrationForm form = new UserRegistrationForm("user", "user@test.com", "password", "token");
        ApplicationUser user = new ApplicationUser("user", "password", "user@test.com",
                Collections.emptyList());
        given(registrationTokenRepository.findByValue(form.getToken()))
                .willReturn(Optional.empty());
        given(userRepository.findByUsername(form.getUsername()))
                .willReturn(Optional.of(user));
        given(userRepository.findByEmail(form.getEmail()))
                .willReturn(Optional.of(user));

        assertThatThrownBy(() -> underTest.registerUser(form))
                  .isInstanceOf(InvalidRegistrationTokenException.class);

        then(userRepository).shouldHaveNoInteractions();
    }

    @Test
    void should_Not_Register_User_And_Throw_UserAlreadyExistsException_When_Username_Taken() {
        UserRegistrationForm form = new UserRegistrationForm("user", "user@test.com", "password", "token");
        ApplicationUser user = new ApplicationUser("user", "password", "user@test.com",
                Collections.emptyList());
        given(registrationTokenRepository.findByValue(form.getToken()))
                .willReturn(Optional.of(new RegistrationToken()));
        given(userRepository.findByUsername(form.getUsername()))
                .willReturn(Optional.of(user));
        given(userRepository.findByEmail(form.getEmail()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.registerUser(form))
                  .isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should(never()).save(any(ApplicationUser.class));
    }

    @Test
    void should_Not_Register_User_And_Throw_UserAlreadyExistsException_When_Email_Taken() {
        UserRegistrationForm form = new UserRegistrationForm("user", "user@test.com", "password", "token");
        ApplicationUser user = new ApplicationUser("user", "password", "user@test.com",
                Collections.emptyList());
        given(registrationTokenRepository.findByValue(form.getToken()))
                .willReturn(Optional.of(new RegistrationToken()));
        given(userRepository.findByUsername(form.getUsername()))
                .willReturn(Optional.empty());
        given(userRepository.findByEmail(form.getEmail()))
                .willReturn(Optional.of(user));

        assertThatThrownBy(() -> underTest.registerUser(form))
                .isInstanceOf(UserAlreadyExistsException.class);

        then(userRepository).should(never()).save(any(ApplicationUser.class));
    }

    @Test
    void should_Change_User_Password() {
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String username = "user";
        ApplicationUser user = new ApplicationUser(username, "oldEncodedPassword", "user@test.com",
                Collections.emptyList());
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn("newEncodedPassword");

        underTest.changeUserPassword(username, oldPassword, newPassword);

        then(userRepository).should().save(userCaptor.capture());
        ApplicationUser updatedUser = userCaptor.getValue();
        assertThat(updatedUser.getPassword()).isEqualTo("newEncodedPassword");
    }

    @Test
    void should_Throw_InvalidPasswordException_When_OldPassword_Invalid() {
        String oldPassword = "wrongPassword";
        String newPassword = "newPassword";
        String username = "user";
        ApplicationUser user = new ApplicationUser(username, "oldEncodedPassword", "user@test.com",
                Collections.emptyList());
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(false);

        assertThatThrownBy(() -> underTest.changeUserPassword(username, oldPassword, newPassword))
                .isInstanceOf(InvalidPasswordException.class);
        then(userRepository).should(never()).save(any(ApplicationUser.class));
    }

    @Test
    void should_Throw_UsernameNotFoundException_When_User_Not_Found() {
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String username = "user";
        ApplicationUser user = new ApplicationUser(username, "oldEncodedPassword", "user@test.com",
                Collections.emptyList());
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.changeUserPassword(username, oldPassword, newPassword))
                .isInstanceOf(UsernameNotFoundException.class);
        then(userRepository).should(never()).save(any(ApplicationUser.class));
    }
}
