package com.example.socialapplication.repositories;

import com.example.socialapplication.model.entity.SavedPost;
import com.example.socialapplication.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPost, String> {
    @Query("SELECT s FROM SavedPost s WHERE s.postId.id = :postId AND s.createBy.username = :username")
    Optional<SavedPost> findByPostIdAndCreateBy(@Param("postId") String postId, @Param("username") String username);
    List<SavedPost> findByCreateBy(Users createdBy);
}