package com.example.backend.user.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PasswordChangeForm {
    @NotNull
    private String oldPassword;
    @NotNull
    @Size(min = 8, max = 128)
    private String newPassword;

    public PasswordChangeForm(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
