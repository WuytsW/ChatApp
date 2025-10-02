package com.example.chatapp.repository;

import com.example.chatapp.model.ChatGroup;
import com.example.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatGroupRepository  extends JpaRepository<ChatGroup, Long> {

    List<ChatGroup> findAllByMembersContaining(User user);
    Optional<ChatGroup> findChatGroupById(Long id);

    Optional<ChatGroup> findByName(String groupName);
}
