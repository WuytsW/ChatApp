package com.example.chatapp.service;

import com.example.chatapp.model.*;
import com.example.chatapp.repository.ChatGroupRepository;
import com.example.chatapp.repository.GroupMessageRepository;
import com.example.chatapp.repository.MessageRepository;
import com.example.chatapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupMessageService {

    private final UserRepository userRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final MessageRepository messageRepository;
    private final GroupMessageRepository groupMessageRepository;

    public GroupMessageService(UserRepository userRepository,
                               ChatGroupRepository chatGroupRepository,
                               MessageRepository messageRepository,
                               GroupMessageRepository groupMessageRepository) {
        this.userRepository = userRepository;
        this.chatGroupRepository = chatGroupRepository;
        this.messageRepository = messageRepository;
        this.groupMessageRepository = groupMessageRepository;
    }

    public GroupMessage sendGroupMessage(String username, Long groupId,  String content) {
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (!group.getMembers().contains(sender)) {
            throw new IllegalArgumentException("User is not a member of this group");
        }

        Message message = new Message(sender, content);
        message = messageRepository.save(message);
        com.example.chatapp.model.GroupMessage groupMessage = new GroupMessage(message, group);
        return groupMessageRepository.save(groupMessage);
    }

    public List<GroupMessage> getAllGroupMessagesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return groupMessageRepository.findAllForUser(user.getId());
    }

    public List<GroupMessage> getGroupMessageByGroup(Long group_id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ChatGroup chatGroup= chatGroupRepository.findChatGroupById(group_id)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if(!chatGroup.getMembers().contains(user)){
            throw new RuntimeException("User not in group");
        }

        return groupMessageRepository.findByChatGroup(chatGroup);
    }

    public GroupMessage markGroupMessageAsRead(Long groupMessageId, String username) {
        GroupMessage groupMessage = groupMessageRepository.findById(groupMessageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!groupMessage.getChatGroup().getMembers().contains(user)) {
            throw new RuntimeException("You are not allowed to modify this message");
        }

        groupMessage.addRead_by_Member(user);

        return groupMessageRepository.save(groupMessage);
    }
}
