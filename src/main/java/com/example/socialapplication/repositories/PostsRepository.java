package com.example.socialapplication.repositories;


import com.example.socialapplication.model.entity.Posts;
import com.example.socialapplication.model.entity.Users;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Posts, String> {
    Page<Posts> findByUserId(Users user, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Posts p WHERE p.id = :postId")
    void deleteById(@NotNull String postId);
    int countByUserId(Users user);

    @Query("SELECT p FROM Posts p JOIN p.favoritesUser u WHERE u.id = :userId")
    Page<Posts> findFavoritesByUserId(String userId, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM favorites WHERE user_id = :userId AND post_id = :postId", nativeQuery = true)
    void deleteFavoriteByUserIdAndPostId(@Param("userId") String userId, @Param("postId") String postId);

    @Query(value = "SELECT COUNT(*) FROM favorites WHERE user_id = :userId AND post_id = :postId", nativeQuery = true)
    int countFavoritesByUserIdAndPostId(String userId, String postId);

    @Query("SELECT p.id FROM Posts p WHERE p.userId.id = :userId")
    List<String> findPostIdsByUserId(@Param("userId") String userId);

    Page<Posts> findByIdIn(List<String> postIds, Pageable pageable);
}