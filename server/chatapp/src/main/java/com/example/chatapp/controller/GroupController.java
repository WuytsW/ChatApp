package com.example.chatapp.controller;


import com.example.chatapp.dto.request.AddGroupMemberRequest;
import com.example.chatapp.dto.response.ChatGroupResponse;
import com.example.chatapp.dto.response.MessageResponse;
import com.example.chatapp.model.ChatGroup;
import com.example.chatapp.security.JwtUser;
import com.example.chatapp.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService){
        this.groupService = groupService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<ChatGroupResponse>> getGroups(@AuthenticationPrincipal JwtUser jwtUser){
        Long userId = jwtUser.getId();
        List<ChatGroup> chatGroups = groupService.getGroupsByUser(userId);
        List<ChatGroupResponse> response = chatGroups.stream().map(ChatGroupResponse::new).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/new")
    public ResponseEntity<ChatGroupResponse> createGroup(@AuthenticationPrincipal JwtUser jwtUser, String name) {
        Long userId = jwtUser.getId();
        ChatGroup chatGroup = groupService.createNew(userId, name);
        ChatGroupResponse response = new ChatGroupResponse(chatGroup);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<ChatGroupResponse> addUserToGroup(
            @AuthenticationPrincipal JwtUser jwtUser,
            @RequestBody AddGroupMemberRequest addGroupMemberRequest
    ) {
        Long userId = jwtUser.getId();
        ChatGroup chatGroup = groupService.addMember(userId, addGroupMemberRequest);
        ChatGroupResponse response = new ChatGroupResponse(chatGroup);
        return ResponseEntity.ok(response);
    }
}
