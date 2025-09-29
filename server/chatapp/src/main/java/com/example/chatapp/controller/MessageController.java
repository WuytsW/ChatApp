package com.example.chatapp.controller;

import com.example.chatapp.dto.SendDirectMessageRequest;
import com.example.chatapp.model.DirectMessage;
import com.example.chatapp.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("")
    public ResponseEntity<List<DirectMessage>> getUserMessages(Authentication auth) {
        String username = auth.getName();
        List<DirectMessage> messages = messageService.getMessagesForUser(username);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<DirectMessage>> getUserUnreadMessages(Authentication auth) {
        String username = auth.getName();
        List<DirectMessage> messages = messageService.getUnreadMessagesForUser(username);
        return ResponseEntity.ok(messages);
    }


    @PostMapping("/send")
    public ResponseEntity<DirectMessage> send(@RequestBody SendDirectMessageRequest req,
                                              Authentication auth) {
        String senderUsername = auth.getName();
        String recipient = req.getRecipient();
        String content = req.getContent();
        DirectMessage dm = messageService.sendMessage(senderUsername, recipient, content);
        return ResponseEntity.ok(dm);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();
        messageService.markAsRead(id, username);
        return ResponseEntity.ok("Message marked as read");
    }

}
