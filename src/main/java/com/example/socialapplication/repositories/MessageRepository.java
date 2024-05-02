package com.example.socialapplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.socialapplication.model.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Bạn có thể thêm các phương thức tùy chỉnh khác ở đây nếu cần
}