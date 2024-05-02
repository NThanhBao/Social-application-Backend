package com.example.socialapplication.service;


import com.example.socialapplication.model.entity.SharesPosts;

import java.util.List;

public interface SharesPostService {
    void createdSharePost(String postId, String currentUsername);
    void deleteSharedPost(String sharesPostId, String currentUsername);
    List<SharesPosts> getSharedPostByCurrentUser(String currentUsername);
    List<SharesPosts> getSharedPostsByUserId(String userId);
}
