package com.example.chatapp.dto.request;

public class SendGroupMessageRequest {
    private Long group_id;
    private String content;

    public Long getGroup_id() {
        return group_id;
    }
    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
