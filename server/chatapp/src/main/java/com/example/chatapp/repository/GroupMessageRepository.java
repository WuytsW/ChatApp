package com.example.chatapp.repository;

import com.example.chatapp.model.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageRepository  extends JpaRepository<GroupMessage, Long> {
}
