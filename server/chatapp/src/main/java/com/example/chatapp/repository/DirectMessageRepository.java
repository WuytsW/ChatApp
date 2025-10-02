package com.example.chatapp.repository;

import com.example.chatapp.model.DirectMessage;
import com.example.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {


    List<DirectMessage> findByRecipient(User recipient);
    List<DirectMessage> findByRecipientAndIsReadFalse(User recipient);
    List<DirectMessage> findByMessageSenderAndRecipientOrderByMessageSentAtAsc(User sender, User recipient);
}
