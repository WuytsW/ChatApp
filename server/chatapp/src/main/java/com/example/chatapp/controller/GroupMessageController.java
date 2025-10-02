package com.example.chatapp.controller;

import com.example.chatapp.dto.request.SendGroupMessageRequest;
import com.example.chatapp.dto.response.MessageResponse;
import com.example.chatapp.model.GroupMessage;
import com.example.chatapp.security.JwtUser;
import com.example.chatapp.service.GroupMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages/group")
public class GroupMessageController {

    private final GroupMessageService groupMessageService;

    public GroupMessageController(GroupMessageService groupMessageService) {
        this.groupMessageService = groupMessageService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<MessageResponse>> getGroupMessages(
            @AuthenticationPrincipal JwtUser user
    ) {
        String username = user.getUsername();
        List<GroupMessage> groupMessages = groupMessageService.getAllGroupMessagesForUser(username);
        List<MessageResponse> response = groupMessages.stream().map(MessageResponse::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/group/{id}")
    public ResponseEntity<List<MessageResponse>> getGroupMessagesByGroup(
            @AuthenticationPrincipal JwtUser user,
            @PathVariable Long id
    ) {
        String username = user.getUsername();
        List<GroupMessage> groupMessages = groupMessageService.getGroupMessageByGroup(id, username);
        List<MessageResponse> response = groupMessages.stream().map(MessageResponse::new).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendGroupMessage(
            @RequestBody SendGroupMessageRequest req,
            @AuthenticationPrincipal JwtUser user
    ) {
        String username = user.getUsername();
        GroupMessage groupMessage = groupMessageService.sendGroupMessage(username, req.getGroup_id(), req.getContent());
        MessageResponse response = new MessageResponse(groupMessage);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/read")
    public ResponseEntity<MessageResponse> markAsRead(
            @AuthenticationPrincipal JwtUser user,
            @RequestParam Long group_message_id
    ) {
        String username = user.getUsername();
        GroupMessage groupMessage = groupMessageService.markGroupMessageAsRead(group_message_id, username);
        MessageResponse response = new MessageResponse(groupMessage);
        return ResponseEntity.ok(response);
    }
}
