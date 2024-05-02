package com.example.socialapplication.service;

import com.example.socialapplication.model.entity.SavedPost;

import java.util.List;

public interface SavedPostService {
    SavedPost savePost(String postId, String currentUsername);
    void deleteSavedPost(String savePostId, String currentUsername);
    List<SavedPost> getSavedPostByCurrentUser(String currentUsername);
    List<SavedPost> getSavedPostsByUserId(String userId);

}
