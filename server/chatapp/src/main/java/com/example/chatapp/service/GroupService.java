package com.example.chatapp.service;

import com.example.chatapp.model.ChatGroup;
import com.example.chatapp.model.User;
import com.example.chatapp.repository.ChatGroupRepository;
import com.example.chatapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    ChatGroupRepository chatGroupRepository;
    UserRepository userRepository;

    public GroupService(ChatGroupRepository chatGroupRepository,
                        UserRepository userRepository){
        this.chatGroupRepository = chatGroupRepository;
        this.userRepository = userRepository;
    }

    public List<ChatGroup> getGroupsByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return chatGroupRepository.findAllByMembersContaining(user);
    }

    public ChatGroup createNew(String username, String name) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ChatGroup chatGroup = new ChatGroup(name);
        chatGroup.addMember(user);
        return chatGroupRepository.save(chatGroup);
    }

    public ChatGroup addMember(String username, String groupName, String memberName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ChatGroup chatGroup = chatGroupRepository.findByName(groupName)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupName));
        User member = userRepository.findByUsername(memberName)
                .orElseThrow(() -> new RuntimeException("New member not found"));

        if(!chatGroup.getMembers().contains(user)){
            throw new RuntimeException("You an only add members if you are a member");
        }

        chatGroup.addMember(member);
        return chatGroupRepository.save(chatGroup);
    }
}
