package com.example.socialapplication.service.Impl;

import com.example.socialapplication.model.entity.Posts;
import com.example.socialapplication.model.entity.SavedPost;
import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.repositories.PostsRepository;
import com.example.socialapplication.repositories.SavedPostRepository;
import com.example.socialapplication.repositories.UsersRepository;
import com.example.socialapplication.service.SavedPostService;
import com.example.socialapplication.util.exception.NotFoundException;
import com.example.socialapplication.util.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SavedPostServiceImpl implements SavedPostService {

    private final SavedPostRepository savedPostRepository;
    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;

    public SavedPostServiceImpl(SavedPostRepository savedPostRepository, UsersRepository usersRepository, PostsRepository postsRepository) {
        this.savedPostRepository = savedPostRepository;
        this.usersRepository = usersRepository;
        this.postsRepository = postsRepository;
    }

    @Override
    public SavedPost savePost(String postId, String currentUsername) {
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }

        Optional<Posts> optionalPost = postsRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new NotFoundException("Không tìm thấy bài viết!");
        }

        Posts post = optionalPost.get();

        SavedPost savedPost = new SavedPost();
        savedPost.setPostId(post);
        savedPost.setCreateBy(currentUser);

        return savedPostRepository.save(savedPost);
    }

    @Override
    public void deleteSavedPost(String savePostId, String currentUsername) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !authentication.getName().equals(currentUsername)) {
            throw new UnauthorizedException("Bạn không có quyền xóa !");
        }

        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }
        Optional<SavedPost> optionalSavedPost = savedPostRepository.findById(savePostId);
        if (optionalSavedPost.isEmpty()) {
            throw new NotFoundException("Bài viết không được lưu!");
        }

        SavedPost savedPost = optionalSavedPost.get();
        savedPostRepository.delete(savedPost);
    }

    @Override
    public List<SavedPost> getSavedPostByCurrentUser(String currentUsername) {
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }
        return savedPostRepository.findByCreateBy(currentUser);
    }
    @Override
    public List<SavedPost> getSavedPostsByUserId(String userId) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }
        return savedPostRepository.findByCreateBy(user);
    }

}
    // Các phương thức khác nếu cần
