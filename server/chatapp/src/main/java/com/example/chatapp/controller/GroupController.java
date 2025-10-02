package com.example.chatapp.controller;


import com.example.chatapp.dto.AddGroupMemberRequest;
import com.example.chatapp.dto.LoginRequest;
import com.example.chatapp.model.ChatGroup;
import com.example.chatapp.model.GroupMessage;
import com.example.chatapp.model.User;
import com.example.chatapp.repository.ChatGroupRepository;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.service.GroupMessageService;
import com.example.chatapp.service.GroupService;
import com.example.chatapp.service.DirectMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService){
        this.groupService = groupService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<ChatGroup>> getGroups(Authentication auth){
        String username = auth.getName();
        List<ChatGroup> chatGroups = groupService.getGroupsByUsername(username);
        return ResponseEntity.ok(chatGroups);
    }

    @PostMapping("/new")
    public ResponseEntity<ChatGroup> createGroup(Authentication auth, String name) {
        String username = auth.getName();
        ChatGroup chatGroup = groupService.createNew(username, name);
        return ResponseEntity.ok(chatGroup);
    }

    @PostMapping("/add")
    public ResponseEntity<ChatGroup> addUserToGroup(
            Authentication auth,
            @RequestBody AddGroupMemberRequest addGroupMemberRequest
    ) {
        String username = auth.getName();
        ChatGroup chatGroup =
                groupService.addMember(username, addGroupMemberRequest.getGroup_name(), addGroupMemberRequest.getMember_name());
        return ResponseEntity.ok(chatGroup);
    }
}
