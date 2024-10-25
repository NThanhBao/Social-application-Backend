package com.example.socialapplication.repositories;

import com.example.socialapplication.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.recipient = :user2) OR (m.sender = :user2 AND m.recipient = :user1) ORDER BY m.createdAt ASC")
    Page<Message> findChatHistoryWithPagination(@Param("user1") String user1, @Param("user2") String user2, Pageable pageable);

}
