package com.example.chatapp.dto.response;

import com.example.chatapp.model.User;

public class UserSummary {
    private Long id;
    private String username;

    public UserSummary() {}

    public UserSummary(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }


    public Long getId() { return id; }
    public String getUsername() { return username; }
}
