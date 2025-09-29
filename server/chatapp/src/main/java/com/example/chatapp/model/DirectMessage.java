package com.example.chatapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "direct_messages")
public class DirectMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Column(nullable = false)
    private boolean isRead = false;


    public Long getId() {
        return id;
    }

    public Message getMessage() {
        return message;
    }
    public void setMessage(Message message) {
        this.message = message;
    }

    public User getRecipient() {
        return recipient;
    }
    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public boolean getIsRead() {
        return isRead;
    }
    public void setIsRead(boolean read) {
        this.isRead = read;
    }
}

