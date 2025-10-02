package com.example.chatapp.controller;

import com.example.chatapp.dto.SendDirectMessageRequest;
import com.example.chatapp.dto.SendGroupMessageRequest;
import com.example.chatapp.model.GroupMessage;
import com.example.chatapp.service.GroupMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages/group")
public class GroupMessageController {

    private final GroupMessageService groupMessageService;

    public GroupMessageController(GroupMessageService groupMessageService) {
        this.groupMessageService = groupMessageService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<GroupMessage>> getGroupMessages(
            Authentication auth
    ) {
        String username = auth.getName();
        List<GroupMessage> messages = groupMessageService.getAllGroupMessagesForUser(username);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/get/group/{id}")
    public ResponseEntity<List<GroupMessage>> getGroupMessagesByGroup(
            Authentication auth,
            @PathVariable Long id
    ) {
        String username = auth.getName();
        List<GroupMessage> messages = groupMessageService.getGroupMessageByGroup(id, username);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/send")
    public ResponseEntity<GroupMessage> sendGroupMessage(
            @RequestBody SendGroupMessageRequest req,
            Authentication auth
    ) {
        String username = auth.getName();
        GroupMessage message = groupMessageService.sendGroupMessage(username, req.getGroup_id(), req.getContent());

        return ResponseEntity.ok(message);
    }

    @PatchMapping("/read")
    public ResponseEntity<GroupMessage> markAsRead(
            Authentication auth,
            @RequestParam Long group_message_id
    ) {
        String username = auth.getName();

        GroupMessage message = groupMessageService.markGroupMessageAsRead(group_message_id, username);
        return ResponseEntity.ok(message);
    }
}
