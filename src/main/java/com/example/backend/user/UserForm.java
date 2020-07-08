package com.example.backend.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserForm {
    @NotNull
    @Size(min = 2, max = 30)
    private String username;
    @NotNull
    @Email
    private String email;
    @NotNull
    @Size(min = 8, max = 256)
    private String password;
    @NotNull
    private String token;

    public UserForm(String username, String email, String password, String token) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }
}
