package com.example.chatapp.service;

import com.example.chatapp.model.*;
import com.example.chatapp.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final DirectMessageRepository directMessageRepository;
    private final UserRepository userRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final GroupMessageRepository groupMessageRepository;

    public MessageService(MessageRepository messageRepository,
                          DirectMessageRepository directMessageRepository,
                          UserRepository userRepository,
                          ChatGroupRepository chatGroupRepository,
                          GroupMessageRepository groupMessageRepository
    ) {
        this.messageRepository = messageRepository;
        this.directMessageRepository = directMessageRepository;
        this.userRepository = userRepository;
        this.chatGroupRepository = chatGroupRepository;
        this.groupMessageRepository = groupMessageRepository;
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

    public List<DirectMessage> getUnreadDirectMessagesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return directMessageRepository.findByRecipientAndIsReadFalse(user);
    }


    public void markDirectMessageAsRead(Long messageId, String username) {
        DirectMessage message = directMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getRecipient().getUsername().equals(username)) {
            throw new RuntimeException("You are not allowed to modify this message");
        }

        message.setIsRead(true);
        directMessageRepository.save(message);
    }

    public GroupMessage sendGroupMessage(Long senderId, Long groupId,  String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (!group.getMembers().contains(sender)) {
            throw new IllegalArgumentException("User is not a member of this group");
        }

        Message message = new Message(sender, content);
        message = messageRepository.save(message);
        GroupMessage groupMessage = new GroupMessage(message, group);
        return groupMessageRepository.save(groupMessage);
    }
}