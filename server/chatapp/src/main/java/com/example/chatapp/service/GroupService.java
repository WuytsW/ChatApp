package com.example.chatapp.service;

import com.example.chatapp.dto.request.AddGroupMemberRequest;
import com.example.chatapp.model.ChatGroup;
import com.example.chatapp.model.User;
import com.example.chatapp.repository.ChatGroupRepository;
import com.example.chatapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    ChatGroupRepository chatGroupRepository;
    UserRepository userRepository;

    public GroupService(ChatGroupRepository chatGroupRepository,
                        UserRepository userRepository){
        this.chatGroupRepository = chatGroupRepository;
        this.userRepository = userRepository;
    }

    public ChatGroup createNew(Long userId, String name, List<Long> newMemberIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        ChatGroup chatGroup = new ChatGroup(name, user);
        chatGroup.addMember(user);

        List<User> users = newMemberIds.stream()
                .map(id -> userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id)))
                .toList();
        chatGroup.addMembers(users);
        return chatGroupRepository.save(chatGroup);
    }

    public ChatGroup addMember(Long userId, AddGroupMemberRequest addGroupMemberRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        Long groupId = addGroupMemberRequest.getGroupId();
        ChatGroup chatGroup = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));
        Long memberId = addGroupMemberRequest.getMemberId();
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("New member not found: " + memberId));

        if(!chatGroup.getMembers().contains(user)){
            throw new RuntimeException("You can only add members if you are a member");
        }

        chatGroup.addMember(member);
        return chatGroupRepository.save(chatGroup);
    }

    public List<ChatGroup> getGroupsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return chatGroupRepository.findAllByMembersContaining(user);
    }
}
