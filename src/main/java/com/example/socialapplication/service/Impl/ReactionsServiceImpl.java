package com.example.socialapplication.service.Impl;

import com.example.socialapplication.model.dto.ReactionsDto;
import com.example.socialapplication.model.dto.UsersInfoDto;
import com.example.socialapplication.model.entity.Comments;
import com.example.socialapplication.model.entity.Posts;
import com.example.socialapplication.model.entity.Reactions;
import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.repositories.CommentsRepository;
import com.example.socialapplication.repositories.PostsRepository;
import com.example.socialapplication.repositories.ReactionsRepository;
import com.example.socialapplication.repositories.UsersRepository;
import com.example.socialapplication.service.ReactionsService;
import com.example.socialapplication.util.exception.NotFoundException;
import com.github.benmanes.caffeine.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReactionsServiceImpl implements ReactionsService {
    private final ReactionsRepository reactionsRepository;
    private final UsersRepository usersRepository;
    private final Cache<String, List<Reactions>> myCache;
    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;
    private static final Logger logger = LoggerFactory.getLogger(CommentsServiceImpl.class);

    public ReactionsServiceImpl(ReactionsRepository reactionsRepository, UsersRepository usersRepository, Cache<String, List<Reactions>> myCache, PostsRepository postsRepository, CommentsRepository commentsRepository) {
        this.reactionsRepository = reactionsRepository;
        this.usersRepository = usersRepository;
        this.myCache = myCache;
        this.postsRepository = postsRepository;
        this.commentsRepository = commentsRepository;
    }

    @Override
    public ResponseEntity<String> createReaction(ReactionsDto reactionDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        Optional<Reactions> existingReaction = reactionsRepository.findByCreatedByAndObjectIdAndType(currentUser, reactionDTO.getObjectId(), reactionDTO.getType());

        if (existingReaction.isPresent()) {
            return ResponseEntity.badRequest().body("Bạn đã thả cảm xúc cho đối tượng này trước đó.");
        } else {
            if ("Posts".equals(reactionDTO.getObjectType())) {
                Optional<Posts> post = postsRepository.findById(reactionDTO.getObjectId());
                if (post.isEmpty()) {
                    throw new NotFoundException("Không tìm thấy bài viết!");
                }

                Posts posts = post.get();
                posts.setTotalLike(posts.getTotalLike() + 1);
                postsRepository.save(posts);

            } else if ("Comments".equals(reactionDTO.getObjectType())) {
                Optional<Comments> comments = commentsRepository.findById(reactionDTO.getObjectId());
                if (comments.isEmpty()) {
                    throw new NotFoundException("Không tìm thấy Comment!");
                }

                Comments comment = comments.get();
                comment.setTotalLike(comment.getTotalLike() + 1);
                commentsRepository.save(comment);
            }

            Reactions reaction = new Reactions();
            reaction.setReactionsId(UUID.randomUUID().toString());
            reaction.setCreatedBy(currentUser);

            reaction.setObjectType(reactionDTO.getObjectType());
            reaction.setObjectId(reactionDTO.getObjectId());
            reaction.setType(reactionDTO.getType());

            reactionsRepository.save(reaction);
            return ResponseEntity.ok("Tạo thành công cảm xúc.");
        }
    }

    @Override
    public void deleteReaction(String reactionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        if (currentUser != null) {
            Optional<Reactions> reactionOptional = reactionsRepository.findById(reactionId);
            if (reactionOptional.isPresent()) {
                Reactions reaction = reactionOptional.get();
                if (reaction.getCreatedBy().equals(currentUser)) {
                    reactionsRepository.delete(reaction);
                } else {
                    throw new IllegalArgumentException("Bạn không thể xóa reactions của người khác.");
                }
            } else {
                throw new IllegalArgumentException("Không tìm thấy ID reactions.");
            }
        } else {
            throw new IllegalStateException("Không thể xác định người dùng hiện tại.");
        }
    }

    @Override
    public void updateReaction(String reactionId, ReactionsDto updatedReactionDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        Optional<Reactions> reactionOptional = reactionsRepository.findById(reactionId);
        if (reactionOptional.isPresent()) {
            Reactions reaction = reactionOptional.get();
            if (reaction.getCreatedBy().equals(currentUser)) {

                reaction.setObjectType(updatedReactionDto.getObjectType());
                reaction.setObjectId(updatedReactionDto.getObjectId());
                reaction.setType(updatedReactionDto.getType());
                reactionsRepository.save(reaction);
                ResponseEntity.ok("Chỉnh sửa cảm xúc thành công.");
            } else {
                throw new IllegalArgumentException("Bạn không thể chỉnh sửa cảm xúc của người khác.");
            }
        } else {
            throw new IllegalArgumentException("Không tìm thấy ID reactions.");
        }
    }

    @Override
    public int getReactionCountByIdPost(String object_id) {
        String cacheKey = "ReactionCountByIdPost_" + object_id;
        List<Reactions> reactions;
        if (myCache.getIfPresent(cacheKey) != null) {
            reactions = myCache.getIfPresent(cacheKey);
        } else {
            reactions = reactionsRepository.findByObjectId(object_id);
            myCache.put(cacheKey, reactions);
        }
        logger.error("LLấy thành công số lượng cảm xúc của bài viết: {}", object_id);
        return reactions.size();
    }

    @Override
    public int getReactionCountByTypeAndObjectId(String object_id, String type) {
        String cacheKey = "ReactionCountByTypeAndObjectId_" + object_id + "_" + type;
        List<Reactions> reactions;
        if (myCache.getIfPresent(cacheKey) != null) {
            reactions = myCache.getIfPresent(cacheKey);
        } else {
            reactions = reactionsRepository.findByObjectIdAndType(object_id, type);
            myCache.put(cacheKey, reactions);
        }
        logger.error("Lấy thành công số lượng tương tác theo cảm xúc: {}", type);
        return reactions.size();
    }

    @Override
    public Page<UsersInfoDto> getUserByReaction(String objectId, String type, Pageable pageable) {
        String cacheKey = objectId + "_" + type;
        List<Reactions> reactions;

        if (myCache.getIfPresent(cacheKey) != null) {
            reactions = myCache.getIfPresent(cacheKey);
        } else {
            reactions = reactionsRepository.findAllByObjectIdAndType(objectId, type);
            myCache.put(cacheKey, reactions);
        }

        if (!reactions.isEmpty()) {
            List<UsersInfoDto> usersDTOList = reactions.stream()
                    .map(reaction -> {
                        Users createdBy = reaction.getCreatedBy();
                        UsersInfoDto usersInfoDto = new UsersInfoDto();
                        usersInfoDto.setId(createdBy.getId());
                        usersInfoDto.setUsername(createdBy.getUsername());
                        usersInfoDto.setMail(createdBy.getMail());
                        usersInfoDto.setGender(createdBy.isGender());
                        usersInfoDto.setFirstName(createdBy.getFirstName());
                        usersInfoDto.setLastName(createdBy.getLastName());
                        usersInfoDto.setAddress(createdBy.getAddress());
                        usersInfoDto.setDateOfBirth(createdBy.getDateOfBirth());
                        usersInfoDto.setPhoneNumber(createdBy.getPhoneNumber());
                        usersInfoDto.setAvatar(createdBy.getAvatar());
                        return usersInfoDto;
                    })
                    .collect(Collectors.toList());

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), usersDTOList.size());
            List<UsersInfoDto> pagedUsersDTOList = usersDTOList.subList(start, end);
            logger.error("Lấy thành công danh sách user theo cảm xúc: {}", type);
            return new PageImpl<>(pagedUsersDTOList, pageable, usersDTOList.size());
        } else {
            throw new IllegalArgumentException("Không tìm thấy cảm xúc cho loại: " + type + " và ID: " + objectId);
        }
    }


    @Override
    public Page<UsersInfoDto> getAllUsersInReactions(Pageable pageable) {
        List<Reactions> reactions;
        String cacheKey = "allUsers";

        if (myCache.getIfPresent(cacheKey) != null) {
            reactions = myCache.getIfPresent(cacheKey);
        } else {
            reactions = reactionsRepository.findAll();
            myCache.put(cacheKey, reactions);
        }
        if (!reactions.isEmpty()) {
        List<UsersInfoDto> usersDTOList = reactions.stream()
                .map(reaction -> {
                    Users createdBy = reaction.getCreatedBy();
                    UsersInfoDto usersInfoDto = new UsersInfoDto();
                    usersInfoDto.setId(createdBy.getId());
                    usersInfoDto.setUsername(createdBy.getUsername());
                    usersInfoDto.setMail(createdBy.getMail());
                    usersInfoDto.setGender(createdBy.isGender());
                    usersInfoDto.setFirstName(createdBy.getFirstName());
                    usersInfoDto.setLastName(createdBy.getLastName());
                    usersInfoDto.setAddress(createdBy.getAddress());
                    usersInfoDto.setDateOfBirth(createdBy.getDateOfBirth());
                    usersInfoDto.setPhoneNumber(createdBy.getPhoneNumber());
                    usersInfoDto.setAvatar(createdBy.getAvatar());
                    return usersInfoDto;
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), usersDTOList.size());
        logger.error("lấy thành công danh sách user");
        return new PageImpl<>(usersDTOList.subList(start, end), pageable, usersDTOList.size());
        } else {
            throw new IllegalArgumentException("Không tìm thấy!");
        }
    }
}
