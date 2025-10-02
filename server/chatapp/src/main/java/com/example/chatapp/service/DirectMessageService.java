package com.example.chatapp.service;

import com.example.chatapp.model.*;
import com.example.chatapp.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DirectMessageService {

    private final MessageRepository messageRepository;
    private final DirectMessageRepository directMessageRepository;
    private final UserRepository userRepository;

    public DirectMessageService(MessageRepository messageRepository,
                                DirectMessageRepository directMessageRepository,
                                UserRepository userRepository
    ) {
        this.messageRepository = messageRepository;
        this.directMessageRepository = directMessageRepository;
        this.userRepository = userRepository;
    }

    public DirectMessage sendDirectMessage(String senderUsername, String recipientUsername, String content) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User recipient = userRepository.findByUsername(recipientUsername)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        Message message = new Message(sender, content);
        message = messageRepository.save(message);
        DirectMessage directMessage = new DirectMessage(message, recipient);
        return directMessageRepository.save(directMessage);
    }


    public List<DirectMessage> getDirectMessagesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return directMessageRepository.findByRecipient(user);
    }

    public List<DirectMessage> getDirectMessagesForMeFromUser(String username, String senderName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User sender = userRepository.findByUsername(senderName)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        return directMessageRepository.findByMessageSenderAndRecipientOrderByMessageSentAtAsc(sender, user);
    }

    public List<DirectMessage> getDirectMessagesForUserFromMe(String username, String recipientName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User recipient = userRepository.findByUsername(recipientName)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        return directMessageRepository.findByMessageSenderAndRecipientOrderByMessageSentAtAsc(user, recipient);
    }



    public List<DirectMessage> getUnreadDirectMessagesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return directMessageRepository.findByRecipientAndIsReadFalse(user);
    }


    public DirectMessage markDirectMessageAsRead(Long messageId, String username) {
        DirectMessage message = directMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getRecipient().getUsername().equals(username)) {
            throw new RuntimeException("You are not allowed to modify this message");
        }

        message.setIsRead(true);
        return directMessageRepository.save(message);
    }



}