package com.example.chatapp.service;

import com.example.chatapp.model.*;
import com.example.chatapp.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
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

    public DirectMessage sendDirectMessage(String senderName, String recipientName, String content) {
        User sender = userRepository.findByUsername(senderName)
                .orElseThrow(() -> new RuntimeException("Sender not found: " + senderName));
        User recipient = userRepository.findByUsername(recipientName)
                .orElseThrow(() -> new RuntimeException("Recipient not found: " + recipientName));

        Message message = new Message(sender, content);
        message = messageRepository.save(message);
        DirectMessage directMessage = new DirectMessage(message, recipient);
        return directMessageRepository.save(directMessage);
    }


    public List<DirectMessage> getDirectMessagesForUser(String recipientName) {
        User user = userRepository.findByUsername(recipientName)
                .orElseThrow(() -> new RuntimeException("User not found: " + recipientName));
        return directMessageRepository.findByRecipient(user);
    }

    public List<DirectMessage> getUnreadDirectMessagesForUser(String recipientName) {
        User user = userRepository.findByUsername(recipientName)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return directMessageRepository.findByRecipientAndIsReadFalse(user);
    }

    public List<DirectMessage> getDirectMessagesForUserFromUser(String recipientName, String senderName) {
        User user = userRepository.findByUsername(recipientName)
                .orElseThrow(() -> new RuntimeException("Recipient not found: " + recipientName));
        User sender = userRepository.findByUsername(senderName)
                .orElseThrow(() -> new RuntimeException("Sender not found: " + senderName));
        return directMessageRepository.findByMessageSenderAndRecipientOrderByMessageSentAtAsc(sender, user);
    }

    public List<DirectMessage> getDirectConversation(String userName1, String userName2){
        List<DirectMessage> messages= new ArrayList<>();
        messages.addAll(getDirectMessagesForUserFromUser(userName1, userName2));
        messages.addAll(getDirectMessagesForUserFromUser(userName2, userName1));
        messages.sort(Comparator.comparing(directMessage -> directMessage.getMessage().getSentAt()));
        return messages;
    }

    public DirectMessage markDirectMessageAsRead(Long messageId, String recipientName) {
        DirectMessage message = directMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));

        if (!message.getRecipient().getUsername().equals(recipientName)) {
            throw new RuntimeException("You are not allowed to modify this message, user: " + recipientName + ", message: " + messageId);
        }

        message.setIsRead(true);
        return directMessageRepository.save(message);
    }


}