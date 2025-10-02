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

    public DirectMessage sendDirectMessage(Long senderId, Long recipientId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found: " + senderId));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found: " + recipientId));

        Message message = new Message(sender, content);
        message = messageRepository.save(message);
        DirectMessage directMessage = new DirectMessage(message, recipient);
        return directMessageRepository.save(directMessage);
    }


    public List<DirectMessage> getDirectMessagesForUser(Long user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found: " + user_id));
        return directMessageRepository.findByRecipient(user);
    }

    public List<DirectMessage> getUnreadDirectMessagesForUser(Long user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found: " + user_id));
        return directMessageRepository.findByRecipientAndIsReadFalse(user);
    }

    public List<DirectMessage> getDirectMessagesForUserFromUser(Long recipientId, Long senderId) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found: " + recipientId));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found: " + senderId));
        return directMessageRepository.findByMessageSenderAndRecipientOrderByMessageSentAtAsc(sender, recipient);
    }

    public List<DirectMessage> getDirectConversation(Long userId1, Long userId2){
        List<DirectMessage> messages= new ArrayList<>();
        messages.addAll(getDirectMessagesForUserFromUser(userId1, userId2));
        messages.addAll(getDirectMessagesForUserFromUser(userId2, userId1));
        messages.sort(Comparator.comparing(directMessage -> directMessage.getMessage().getSentAt()));
        return messages;
    }

    public DirectMessage markDirectMessageAsRead(Long messageId, Long recipientId) {
        DirectMessage message = directMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));

        if (!message.getRecipient().getId().equals(recipientId)) {
            throw new RuntimeException("You are not allowed to modify this message, user: " + recipientId + ", message: " + messageId);
        }

        message.setIsRead(true);
        return directMessageRepository.save(message);
    }


}