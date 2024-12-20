package com.example.socialapplication.repositories;

import com.example.socialapplication.model.entity.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, String> {

    @Query("SELECT c FROM Comments c WHERE c.postId.id = :postId")
    Page<Comments> findByPostId(@Param("postId") String postId, Pageable pageable);

    @Query("SELECT c FROM Comments c WHERE c.postId.id IN :postIds")
    Page<Comments> findByPostIdIn(@Param("postIds") List<String> postIds, Pageable pageable);

    @Query("SELECT c.id FROM Comments c WHERE c.createBy.id = :userId")
    List<String> findCommentIdsByUserId(@Param("userId") String userId);
}

