package com.example.chatapp.dto;

import com.example.chatapp.model.User;

public class UserSummary {
    private Long id;
    private String username;

    public UserSummary() {}

    public UserSummary(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
