package com.example.chatapp.dto.response;

import com.example.chatapp.model.ChatGroup;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChatGroupResponse {

    private Long id;
    private String name;
    private List<UserSummary> members;
    private Instant createdAt;

    public ChatGroupResponse() {}

    public ChatGroupResponse(ChatGroup chatGroup) {
        this.id = chatGroup.getId();
        this.name = chatGroup.getName();
        this.members = chatGroup.getMembers().stream().map(UserSummary::new).toList();
        this.createdAt = chatGroup.getCreatedAt();
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public List<UserSummary> getMembers() {
        return members;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
}
