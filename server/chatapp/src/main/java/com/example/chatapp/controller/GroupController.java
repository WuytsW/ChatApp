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
    public ResponseEntity<List<ChatGroupResponse>> getGroups(@AuthenticationPrincipal JwtUser user){
        String username = user.getUsername();
        List<ChatGroup> chatGroups = groupService.getGroupsByUsername(username);
        List<ChatGroupResponse> response = chatGroups.stream().map(ChatGroupResponse::new).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/new")
    public ResponseEntity<ChatGroupResponse> createGroup(@AuthenticationPrincipal JwtUser user, String name) {
        String username = user.getUsername();
        ChatGroup chatGroup = groupService.createNew(username, name);
        ChatGroupResponse response = new ChatGroupResponse(chatGroup);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<ChatGroupResponse> addUserToGroup(
            @AuthenticationPrincipal JwtUser user,
            @RequestBody AddGroupMemberRequest addGroupMemberRequest
    ) {
        String username = user.getUsername();
        ChatGroup chatGroup = groupService.addMember(username, addGroupMemberRequest);
        ChatGroupResponse response = new ChatGroupResponse(chatGroup);
        return ResponseEntity.ok(response);
    }
}
