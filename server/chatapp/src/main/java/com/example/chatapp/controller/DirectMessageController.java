package com.example.chatapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @GetMapping("/get/sender/{sender}")
    public ResponseEntity<?> getDirectMessagesFromSender(Authentication auth, @PathVariable String sender) {
        String username = auth.getName();
        List<DirectMessage> messages = directMessageService.getDirectMessagesForUserFromUser(username, sender);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/get/conversation{username2}")
    public ResponseEntity<?> getDirectConversation(Authentication auth, @PathVariable String username2) {
        String username1 = auth.getName();
        List<DirectMessage> messages = directMessageService.getDirectConversation(username1, username2);
        return ResponseEntity.ok(messages);
    }

    private static final Logger log = LoggerFactory.getLogger(DirectMessageController.class);

    @GetMapping("/get/recipient/{recipient}")
    public ResponseEntity<?> getDirectMessagesForRecipient(Authentication auth, @PathVariable String recipient) {
        String username = auth.getName();
        log.debug(recipient);
        List<DirectMessage> messages = directMessageService.getDirectMessagesForUserFromUser(recipient, username);
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
    public ResponseEntity<?> markDirectMessageAsRead(Authentication auth, @RequestParam Long id) {
        String username = auth.getName();
        DirectMessage directMessage = directMessageService.markDirectMessageAsRead(id, username);
        return ResponseEntity.ok(directMessage);
    }
}
