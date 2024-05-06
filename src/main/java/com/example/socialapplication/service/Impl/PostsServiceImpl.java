package com.example.socialapplication.service.Impl;

import com.example.socialapplication.model.dto.PostsDto;
import com.example.socialapplication.model.entity.Medias;
import com.example.socialapplication.model.entity.Posts;
import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.repositories.MediaRepository;
import com.example.socialapplication.repositories.PostsRepository;
import com.example.socialapplication.repositories.UsersRepository;
import com.example.socialapplication.service.PostsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PostsServiceImpl implements PostsService {
    private PostsRepository postsRepository;
    private UsersRepository usersRepository;
    private final MediaRepository mediaRepository;
    private static final Logger logger = LoggerFactory.getLogger(PostsServiceImpl.class);

    @Override
    public Posts createPosts(PostsDto postDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        logger.info("Current username: {}", currentUsername);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        String userId = currentUser != null ? currentUser.getId() : null;
        logger.info("Current user ID: {}", userId);

        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }

        Posts post = new Posts();
        post.setId(UUID.randomUUID().toString());
        post.setTitle(postDto.getTitle());
        post.setBody(postDto.getBody());
        post.setStatus(postDto.getStatus());
        post.setTotalLike(0);
        post.setTotalComment(0);
        post.setTotalShare(0);
        post.setUserId(currentUser);
        post.setCreateAt(new Timestamp(System.currentTimeMillis()));

        // Thêm media vào danh sách của bài viết
        List<Medias> medias = new ArrayList<>();
        for (String mediaId : postDto.getMediasId()) {
            Optional<Medias> mediaOptional = mediaRepository.findById(mediaId);
            if (mediaOptional.isPresent()) {
                Medias media = mediaOptional.get();
                // Cập nhật trường postsId của media
                media.setPostsId(post);
                medias.add(media);
            }
        }
        post.setMedias(medias);

        Posts createdPost = postsRepository.save(post);
        logger.info("New post created with ID: {}", createdPost.getId());

        logger.info("Post creation process completed successfully");

        return createdPost;
    }


    @Override
    public void updatePost(UUID postId, PostsDto updatedPost) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        if (currentUser == null) {
            throw new EntityNotFoundException("Không tìm thấy người dùng hiện tại");
        }
        Optional<Posts> optionalPost = postsRepository.findById(postId.toString());
        if (!optionalPost.isPresent()) {
            throw new EntityNotFoundException("Không tìm thấy bài đăng có ID: " + postId);
        }
        Posts post = optionalPost.get();
        if (!post.getUserId().equals(currentUser)) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật bài đăng này");
        }

        post.setTitle(updatedPost.getTitle());
        post.setBody(updatedPost.getBody());
        post.setStatus(updatedPost.getStatus());
        post.setCreateAt(new Timestamp(System.currentTimeMillis()));

        List<String> mediasIdStrings = updatedPost.getMediasId();
        List<Medias> medias = new ArrayList<>();
        for (String mediaId : mediasIdStrings) {
            Optional<Medias> mediaOptional  = mediaRepository.findById(mediaId);
            if (mediaOptional.isPresent()) {
                Medias media = mediaOptional.get();
                media.setPostsId(post);
                medias.add(media);
            }
        }
        post.setMedias(medias);
        postsRepository.save(post);
        logger.info("Bài đăng với ID {} đã được cập nhật thành công", postId);
    }

    @Override
    public void deletePost(UUID postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        if (currentUser == null) {
            throw new EntityNotFoundException("Không tìm thấy người dùng hiện tại");
        }

        Optional<Posts> optionalPost = postsRepository.findById(postId.toString());
        if (!optionalPost.isPresent()) {
            throw new EntityNotFoundException("Không tìm thấy bài đăng có ID: " + postId);
        }

        Posts post = optionalPost.get();
        if (!post.getUserId().equals(currentUser)) {
            throw new AccessDeniedException("Bạn không có quyền Xóa bài đăng này");
        }

        postsRepository.deleteById(postId.toString());

        logger.info("Bài đăng với ID {} đã được xóa thành công", postId);
    }

    @Override
    public Page<Posts> getPostsByUserId(Pageable pageable, UUID userId) {
        Optional<Users> userOptional = usersRepository.findById(String.valueOf(userId));
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            return postsRepository.findByUserId(user, pageable);
        } else {
            logger.error("User not found with ID: {}", userId);
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
    }

    @Override
    public int getNumberOfPostsByUserId(UUID userId) {
        Optional<Users> userOptional = usersRepository.findById(String.valueOf(userId));
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            return postsRepository.countByUserId(user);
        } else {
            logger.error("User not found with ID: {}", userId);
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
    }

    @Override
    public List<Posts> getAllPosts(Pageable pageable) {
        List<Posts> allPosts = postsRepository.findAll();
        logger.info("Retrieved {} posts", allPosts.size());
        return allPosts;
    }

    @Override
    public int getNumberOfPosts() {
        List<Posts> allPosts = postsRepository.findAll();
        logger.info("Number of posts: {}", allPosts.size());
        return allPosts.size();
    }

    @Override
    public Page<Posts> getListOfPostsByLoggedInUser(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Users users = usersRepository.findByUsername(currentUsername);
        if (users != null) {
            return postsRepository.findByUserId(users, pageable);
        } else {
            logger.error("User not found with username: {}", currentUsername);
            throw new EntityNotFoundException("User not found with username: " + currentUsername);
        }
    }

    @Override
    public int getNumberOfPostsByLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Users users = usersRepository.findByUsername(currentUsername);
        if (users != null) {
            int numberOfPosts = postsRepository.countByUserId(users);
            logger.info("Number of posts by logged-in user {}: {}", currentUsername, numberOfPosts);
            return numberOfPosts;
        } else {
            logger.error("User not found with username: {}", currentUsername);
            throw new EntityNotFoundException("User not found with username: " + currentUsername);
        }
    }
}