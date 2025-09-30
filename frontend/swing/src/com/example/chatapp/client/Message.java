package com.example.chatapp.client;

public class Message {
    private final Long id;
    private final String senderUsername;
    private final String recipientUsername;
    private final String content;
    private final String sentAt; // keep as string for simplicity
    private final boolean read;

    public Message(Long id, String senderUsername, String recipientUsername, String content, String sentAt, boolean read) {
        this.id = id;
        this.senderUsername = senderUsername;
        this.recipientUsername = recipientUsername;
        this.content = content;
        this.sentAt = sentAt;
        this.read = read;
    }

    public Long getId() { return id; }
    public String getSenderUsername() { return senderUsername; }
    public String getRecipientUsername() { return recipientUsername; }
    public String getContent() { return content; }
    public String getSentAt() { return sentAt; }
    public boolean isRead() { return read; }

    @Override public String toString() {
        String who = senderUsername + " → " + recipientUsername;
        String status = read ? "✓" : "•";
        return "[" + status + "] " + who + "  " + (sentAt != null ? sentAt : "") + "  ::  " + content;
    }
}
