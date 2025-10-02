package com.example.chatapp.controller;

import com.example.chatapp.dto.response.MessageResponse;
import com.example.chatapp.dto.request.SendDirectMessageRequest;
import com.example.chatapp.model.DirectMessage;
import com.example.chatapp.security.JwtUser;
import com.example.chatapp.security.JwtUtil;
import com.example.chatapp.service.DirectMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.List;

@RestController
@RequestMapping("/api/messages/direct")
public class DirectMessageController {

    private final DirectMessageService directMessageService;

    public DirectMessageController(DirectMessageService directMessageService) {
        this.directMessageService = directMessageService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<MessageResponse>> getDirectMessages(@AuthenticationPrincipal JwtUser user) {
        Long userId = user.getId();
        List<DirectMessage> messages = directMessageService.getDirectMessagesForUser(userId);
        List<MessageResponse> response = messages.stream().map(MessageResponse::new).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/unread")
    public ResponseEntity<List<MessageResponse>> getUserUnreadMessages(@AuthenticationPrincipal JwtUser user) {
        Long userId = user.getId();
        List<DirectMessage> messages = directMessageService.getUnreadDirectMessagesForUser(userId);
        List<MessageResponse> response = messages.stream().map(MessageResponse::new).toList();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/get/conversation{userId2}")
    public ResponseEntity<List<MessageResponse>> getDirectConversation(@AuthenticationPrincipal JwtUser user, @PathVariable Long userId2) {
        Long userId1 = user.getId();
        List<DirectMessage> messages = directMessageService.getDirectConversation(userId1, userId2);
        List<MessageResponse> response = messages.stream().map(MessageResponse::new).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> send(@RequestBody SendDirectMessageRequest req, @AuthenticationPrincipal JwtUser user)
    {
        Long senderId = user.getId();
        Long recipientId = req.getRecipientId();
        String content = req.getContent();
        DirectMessage directMessage = directMessageService.sendDirectMessage(senderId, recipientId, content);
        MessageResponse response = new MessageResponse(directMessage);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/read")
    public ResponseEntity<MessageResponse> markDirectMessageAsRead(@AuthenticationPrincipal JwtUser user, @RequestParam Long id) {
        Long userId = user.getId();
        DirectMessage directMessage = directMessageService.markDirectMessageAsRead(id, userId);
        MessageResponse response = new MessageResponse(directMessage);
        return ResponseEntity.ok(response);
    }
}
