package com.example.socialapplication.repositories;

import com.example.socialapplication.model.entity.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, String> {
    @Query("SELECT CONCAT(c.createBy.firstName, ' ', c.createBy.lastName)" +
            ", c.content, c.createBy.id" +
            ", c.createBy.avatar" +
            ", c.createAt" +
            ", c.totalLike" +
            " FROM Comments c WHERE c.postId.id = :postId ORDER BY c.createAt DESC")
    Page<Comments> findByPostId(@Param("postId") String postId, Pageable pageable);

}
