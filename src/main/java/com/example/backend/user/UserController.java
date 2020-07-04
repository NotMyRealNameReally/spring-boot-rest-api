package com.example.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public void registerUser(@RequestBody UserDto user) {
        customUserDetailsService.registerUser(user);
    }
}
