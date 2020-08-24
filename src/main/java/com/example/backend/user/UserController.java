package com.example.backend.user;

import java.util.List;

import javax.validation.Valid;

import com.example.backend.user.form.PasswordChangeForm;
import com.example.backend.user.form.UserRegistrationForm;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<ApplicationUser> findAllUsers() {
        return userService.findAllUsers();
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody @Valid UserRegistrationForm user) {
        userService.registerUser(user);
    }

    @PostMapping("/change-password")
    public void changeUserPassword(Authentication authentication, @RequestBody @Valid PasswordChangeForm form) {
        userService.changeUserPassword(authentication.getName(), form.getOldPassword(), form.getNewPassword());
    }
}
