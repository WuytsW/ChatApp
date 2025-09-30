package com.example.chatapp.controller;

import com.example.chatapp.dto.SendDirectMessageRequest;
import com.example.chatapp.model.DirectMessage;
import com.example.chatapp.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class DirectMessageController {

    private final MessageService messageService;

    public DirectMessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("")
    public ResponseEntity<List<DirectMessage>> getUserMessages(Authentication auth) {
        String username = auth.getName();
        List<DirectMessage> messages = messageService.getDirectMessagesForUser(username);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<DirectMessage>> getUserUnreadMessages(Authentication auth) {
        String username = auth.getName();
        List<DirectMessage> messages = messageService.getUnreadDirectMessagesForUser(username);
        return ResponseEntity.ok(messages);
    }


    @PostMapping("/send")
    public ResponseEntity<DirectMessage> send(@RequestBody SendDirectMessageRequest req,
                                              Authentication auth) {
        String sender = auth.getName();
        String recipient = req.getRecipient();
        String content = req.getContent();
        DirectMessage dm = messageService.sendDirectMessage(sender, recipient, content);
        return ResponseEntity.ok(dm);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();
        messageService.markDirectMessageAsRead(id, username);
        return ResponseEntity.ok("Message marked as read");
    }

}
