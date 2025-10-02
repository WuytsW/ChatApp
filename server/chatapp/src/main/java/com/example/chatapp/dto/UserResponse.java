package com.example.chatapp.dto;

import com.example.chatapp.model.User;
import java.util.List;
import java.util.stream.Collectors;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private List<UserSummary> friends;
    private String role;

    public UserResponse() {}

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.friends = user.getFriends().stream()
                .map(UserSummary::new)
                .collect(Collectors.toList());
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<UserSummary> getFriends() { return friends; }
    public void setFriends(List<UserSummary> friends) { this.friends = friends; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}


