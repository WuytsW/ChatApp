package com.example.chatapp.controller;

import com.example.chatapp.dto.SendDirectMessageRequest;
import com.example.chatapp.model.DirectMessage;
import com.example.chatapp.service.DirectMessageService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<DirectMessage>> getUserMessages(Authentication auth) {
        String username = auth.getName();
        List<DirectMessage> messages = directMessageService.getDirectMessagesForUser(username);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/get/unread")
    public ResponseEntity<List<DirectMessage>> getUserUnreadMessages(Authentication auth) {
        String username = auth.getName();
        List<DirectMessage> messages = directMessageService.getUnreadDirectMessagesForUser(username);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/get/sender")
    public ResponseEntity<?> getDirectMessagesFromSender(Authentication auth, String sender) {
        String username = auth.getName();
        List<DirectMessage> messages = directMessageService.getDirectMessagesForMeFromUser(username, sender);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/get/recipient")
    public ResponseEntity<?> getDirectMessagesForRecipient(Authentication auth, String recipient) {
        String username = auth.getName();
        List<DirectMessage> messages = directMessageService.getDirectMessagesForUserFromMe(username, recipient);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/send")
    public ResponseEntity<DirectMessage> send(
            @RequestBody SendDirectMessageRequest req,
            Authentication auth
    ) {
        String sender = auth.getName();
        String recipient = req.getRecipient();
        String content = req.getContent();
        DirectMessage dm = directMessageService.sendDirectMessage(sender, recipient, content);
        return ResponseEntity.ok(dm);
    }

    @PatchMapping("/read")
    public ResponseEntity<?> markDirectMessageAsRead(Authentication auth, Long id) {
        String username = auth.getName();
        DirectMessage directMessage = directMessageService.markDirectMessageAsRead(id, username);
        return ResponseEntity.ok(directMessage);
    }
}
