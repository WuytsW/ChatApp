package com.example.chatapp.repository;

import com.example.chatapp.model.ChatGroup;
import com.example.chatapp.model.GroupMessage;
import com.example.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMessageRepository  extends JpaRepository<GroupMessage, Long> {


    @Query(
    """
        select gm
        from GroupMessage gm
          join fetch gm.message m
          join gm.chatGroup g
          join g.members mem
        where mem = :user
        order by m.sentAt desc
    """)
    List<GroupMessage> findAllForUser(@Param("user") User user);


    List<GroupMessage> findByChatGroup(ChatGroup chatGroup);
}
