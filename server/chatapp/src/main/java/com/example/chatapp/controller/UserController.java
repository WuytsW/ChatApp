package com.example.chatapp.controller;

import com.example.chatapp.model.User;
import com.example.chatapp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @GetMapping("/get")
    public User getUser(Authentication auth){
        String username = auth.getName();
        return userService.getUserByUsername(username);
    }

    @GetMapping("/get/user")
    public User getUserByUsername(String username){
        return userService.getUserByUsername(username);
    }

    @PostMapping("/friends/add")
    public User addFriend(Authentication auth, String friend_name){
        String username = auth.getName();
        return userService.addFriend(username, friend_name);
    }
}
