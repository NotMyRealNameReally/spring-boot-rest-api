package com.example.backend.user;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @GetMapping
    public List<ApplicationUser> findAllUsers() {
        return customUserDetailsService.findAllUsers();
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody @Valid UserForm user) {
        customUserDetailsService.registerUser(user);
    }
}
