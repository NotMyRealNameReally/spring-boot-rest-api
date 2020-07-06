package com.example.backend.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    @PostMapping("/register-token")
    public String generateRegistrationToken() {
        return adminService.generateRegistrationToken();
    }
}
