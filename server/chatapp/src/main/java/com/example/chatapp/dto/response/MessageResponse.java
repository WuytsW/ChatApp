package com.example.chatapp.dto.response;

import com.example.chatapp.model.DirectMessage;
import com.example.chatapp.model.GroupMessage;
import com.example.chatapp.model.Message;

import java.time.LocalDateTime;
import java.util.List;

public class MessageResponse {

    private Long id;
    private UserSummary sender;
    private UserSummary recipient;
    private String content;
    private LocalDateTime sentAt;
    private boolean isRead;
    private ChatGroupResponse group;
    private List<UserSummary> isReadBy;
    private String type;

    public MessageResponse() {}

    public MessageResponse(DirectMessage dm) {
        this.id = dm.getId();
        this.sender = new UserSummary(dm.getMessage().getSender());
        this.recipient = new UserSummary(dm.getRecipient());
        this.content = dm.getMessage().getContent();
        this.sentAt = dm.getMessage().getSentAt();
        this.isRead = dm.getIsRead();
        this.group = null;
        this.isReadBy = null;
        this.type = "DM";
    }

    public MessageResponse(GroupMessage gm) {
        this.id = gm.getId();
        this.sender = new UserSummary(gm.getMessage().getSender());
        this.recipient = null;
        this.content = gm.getMessage().getContent();
        this.sentAt = gm.getMessage().getSentAt();
        this.isRead = false; //See what to do here
        this.group = new ChatGroupResponse(gm.getChatGroup());
        this.isReadBy = gm.getRead_by_members().stream().map(UserSummary::new).toList();
        this.type = "GM";
    }

    public MessageResponse(Message m){
        this.id = m.getId();
        this.sender = new UserSummary(m.getSender());
        this.recipient = null;
        this.content = m.getContent();
        this.sentAt = m.getSentAt();
        this.isRead = false; //See what to do here
        this.group = null;
        this.isReadBy = null;
        this.type = "M";
    }


    public Long getId() { return id; }
    public UserSummary getSender() { return sender; }
    public UserSummary getRecipient() { return recipient; }
    public String getContent() { return content; }
    public LocalDateTime getSentAt() { return sentAt; }
    public boolean isRead() { return isRead; }
    public ChatGroupResponse getGroup() {
        return group;
    }
    public List<UserSummary> getIsReadBy() {
        return isReadBy;
    }
    public String getType() {
        return type;
    }
}
