package com.example.chatapp.controller;


import com.example.chatapp.model.ChatGroup;
import com.example.chatapp.model.DirectMessage;
import com.example.chatapp.model.GroupMessage;
import com.example.chatapp.model.User;
import com.example.chatapp.repository.ChatGroupRepository;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.service.GroupService;
import com.example.chatapp.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    ChatGroupRepository chatGroupRepository;
    UserRepository userRepository;
    MessageService messageService;

    public GroupController(ChatGroupRepository chatGroupRepository, UserRepository userRepository, MessageService messageService){
        this.chatGroupRepository = chatGroupRepository;
        this.userRepository = userRepository;
        this.messageService = messageService;
    }

    @PostMapping("/new")
    public ResponseEntity<ChatGroup> getUserUnreadMessages(Authentication auth, ChatGroup group) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username).get();
        group.addMember(user);
        ChatGroup chatGroup = chatGroupRepository.save(group);

        return ResponseEntity.ok(chatGroup);
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<GroupMessage> sendGroupMessage(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication auth
    ) {
        String username = auth.getName();
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        String content = body.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        GroupMessage message = messageService.sendGroupMessage(sender.getId(), id, content);
        return ResponseEntity.ok(message);
    }
}
