package com.example.chatapp.dto;

public class LoginRequest {
    private String email_or_password;
    private String password;

    public String getEmail_or_password() {
        return email_or_password;
    }
    public void setEmail_or_password(String email_or_password) {
        this.email_or_password = email_or_password;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


}
