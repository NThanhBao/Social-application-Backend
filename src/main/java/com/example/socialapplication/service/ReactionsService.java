package com.example.socialapplication.service;


import com.example.socialapplication.model.dto.ReactionsDto;
import com.example.socialapplication.model.dto.UsersInfoDto;
import com.example.socialapplication.model.entity.Posts;
import com.example.socialapplication.model.entity.Reactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;



public interface ReactionsService {
    ResponseEntity<String> createReaction(ReactionsDto reactionDTO);

    void deleteReaction(String objectId);

    int getReactionCountByIdPost(String object_id);

    int getReactionCountByTypeAndObjectId(String object_id, String type);

    void updateReaction(String reactionId, ReactionsDto updatedReactionDto);

    Page<UsersInfoDto> getUserByReaction(String objectId, String type, Pageable pageable);

    Page<UsersInfoDto> getAllUsersInReactions(Pageable pageable);

    Page<Reactions> getAllReactionsOnCurrentUserPosts(Pageable pageable);

    Page<Reactions> getAllReactionsOnCurrentUserComments(Pageable pageable);

    Page<Reactions> getAllReactionsOfCurrentUser(Pageable pageable);
}
