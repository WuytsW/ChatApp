package com.example.chatapp.controller;

import com.example.chatapp.dto.request.SendGroupMessageRequest;
import com.example.chatapp.dto.response.MessageResponse;
import com.example.chatapp.model.GroupMessage;
import com.example.chatapp.security.JwtUser;
import com.example.chatapp.service.GroupMessageService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<MessageResponse>> getGroupMessages(@AuthenticationPrincipal JwtUser jwtUser) {
        Long userId = jwtUser.getId();
        List<GroupMessage> groupMessages = groupMessageService.getAllGroupMessagesForUser(userId);
        List<MessageResponse> response = groupMessages.stream().map(MessageResponse::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/group/{id}")
    public ResponseEntity<List<MessageResponse>> getGroupMessagesByGroup(@AuthenticationPrincipal JwtUser jwtUser, @PathVariable Long id) {
        Long userId = jwtUser.getId();
        List<GroupMessage> groupMessages = groupMessageService.getGroupMessageByGroup(id, userId);
        List<MessageResponse> response = groupMessages.stream().map(MessageResponse::new).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendGroupMessage(@AuthenticationPrincipal JwtUser jwtUser, @RequestBody SendGroupMessageRequest req) {
        Long userId = jwtUser.getId();
        GroupMessage groupMessage = groupMessageService.sendGroupMessage(userId, req.getGroupId(), req.getContent());
        MessageResponse response = new MessageResponse(groupMessage);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/read")
    public ResponseEntity<MessageResponse> markAsRead(@AuthenticationPrincipal JwtUser jwtUser, @RequestParam Long groupMessageId) {
        Long userId = jwtUser.getId();
        GroupMessage groupMessage = groupMessageService.markGroupMessageAsRead(groupMessageId, userId);
        MessageResponse response = new MessageResponse(groupMessage);
        return ResponseEntity.ok(response);
    }
}
