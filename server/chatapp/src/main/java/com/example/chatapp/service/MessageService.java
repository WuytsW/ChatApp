package com.example.chatapp.service;

import com.example.chatapp.model.DirectMessage;
import com.example.chatapp.model.Message;
import com.example.chatapp.model.User;
import com.example.chatapp.repository.DirectMessageRepository;
import com.example.chatapp.repository.MessageRepository;
import com.example.chatapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final DirectMessageRepository directMessageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository,
                          DirectMessageRepository directMessageRepository,
                          UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.directMessageRepository = directMessageRepository;
        this.userRepository = userRepository;
    }

    public DirectMessage sendMessage(String senderUsername, String recipientUsername, String content) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User recipient = userRepository.findByUsername(recipientUsername)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        // Create and save Message
        Message message = new Message(sender, content);
        message = messageRepository.save(message);

        // Wrap into DirectMessage
        DirectMessage directMessage = new DirectMessage(message, recipient);
        return directMessageRepository.save(directMessage);
    }


    public List<DirectMessage> getMessagesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return directMessageRepository.findByRecipient(user);
    }

    public List<DirectMessage> getUnreadMessagesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return directMessageRepository.findByRecipientAndIsReadFalse(user);
    }


    public void markAsRead(Long messageId, String username) {
        DirectMessage message = directMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getRecipient().getUsername().equals(username)) {
            throw new RuntimeException("You are not allowed to modify this message");
        }

        message.setIsRead(true);
        directMessageRepository.save(message);
    }
}