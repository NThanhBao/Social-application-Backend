package com.example.socialapplication.service;

import com.example.socialapplication.model.dto.PostsDto;
import com.example.socialapplication.model.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PostsService {

    Posts createPosts(PostsDto post);

    void updatePost(UUID postId, PostsDto updatedPost);

    void deletePost(UUID postId);

    Page<Posts> getPostsByUserId(Pageable pageable, UUID userId);

    int getNumberOfPostsByUserId(UUID userId);

    List<Posts> getAllPosts(Pageable pageable);

    int getNumberOfPosts();

    Page<Posts> getListOfPostsByLoggedInUser(Pageable pageable);

    int getNumberOfPostsByLoggedInUser();
}