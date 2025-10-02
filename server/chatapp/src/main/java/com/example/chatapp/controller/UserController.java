package com.example.chatapp.controller;

import com.example.chatapp.dto.request.RegisterRequest;
import com.example.chatapp.dto.response.UserSummary;
import com.example.chatapp.model.User;
import com.example.chatapp.dto.response.UserResponse;
import com.example.chatapp.security.JwtUser;
import com.example.chatapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest registerRequest) {
        User user = userService.register(registerRequest);
        return ResponseEntity.ok(new UserResponse(user));
    }

    @GetMapping("/get")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal JwtUser jwtUser){
        String username = jwtUser.getUsername();
        User userU = userService.getUserByUsername(username);
        return ResponseEntity.ok(new UserResponse(userU));
    }

    @GetMapping("/get/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(new UserResponse(user));
    }

    @GetMapping("/friends/get")
    public ResponseEntity<List<UserSummary>> getFriends(@AuthenticationPrincipal JwtUser user){
        String username = user.getUsername();
        List<User> friends = userService.getFriends(username);
        List<UserSummary> friendsSummary = friends.stream().map(UserSummary::new).toList();
        return ResponseEntity.ok(friendsSummary);
    }

    @PostMapping("/friends/add")
    public ResponseEntity<UserResponse> addFriend(@AuthenticationPrincipal JwtUser jwtUser, @RequestParam String friend_name){
        String username = jwtUser.getUsername();
        User userU = userService.addFriend(username, friend_name);
        return ResponseEntity.ok(new UserResponse(userU));
    }
}
