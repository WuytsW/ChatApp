package com.example.chatapp.dto.request;

public class SendGroupMessageRequest {
    private Long groupId;
    private String content;

    public Long getGroupId() {
        return groupId;
    }
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
