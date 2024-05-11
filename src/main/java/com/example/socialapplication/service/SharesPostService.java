package com.example.socialapplication.service;


import com.example.socialapplication.model.dto.SharesPostDto;
import com.example.socialapplication.model.entity.SharesPosts;

import java.util.List;

public interface SharesPostService {

    SharesPosts createdSharePost(SharesPostDto sharesPostDto);

    void deleteSharedPost(String sharesPostId, String currentUsername);

    List<SharesPosts> getSharedPostByCurrentUser(String currentUsername);

    List<SharesPosts> getListSharedPostsByPostId(String postId);

}
