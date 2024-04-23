package com.example.socialapplication.repositories;


import com.example.socialapplication.model.entity.Posts;
import com.example.socialapplication.model.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostsRepository extends JpaRepository<Posts, String> {
    Page<Posts> findByUserId(Users user, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Posts p WHERE p.id = :postId")
    void deleteById(String postId);
    int countByUserId(Users user);
}