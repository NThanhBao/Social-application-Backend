package com.example.socialapplication.service;

import com.example.socialapplication.model.dto.UsersInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowService {
    void followUser(String followingUserId);
    int getFollowingCount();
    int getFollowerCount();
    void unfollowUser(String followingUserId);

    int getFollowingCount(String username);

    int getFollowerCount(String username);

    Page<UsersInfoDto> getFollowingListUsers(Pageable pageable);
    Page<UsersInfoDto> getFollowerListUsers(Pageable pageable);
}