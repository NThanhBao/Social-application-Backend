package com.example.socialapplication.repositories;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.socialapplication.model.entity.Message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findBySenderAndRecipientOrRecipientAndSender(
            String sender, String recipient, String recipient2, String sender2, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.recipient = :user2) OR (m.sender = :user2 AND m.recipient = :user1) ORDER BY m.createdAt ASC")
    Page<Message> findChatHistory(@Param("user1") String user1, @Param("user2") String user2, Pageable pageable);

}
