package com.example.socialapplication.service.Impl;

import com.example.socialapplication.model.entity.Posts;
import com.example.socialapplication.model.entity.SharesPosts;
import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.repositories.PostsRepository;
import com.example.socialapplication.repositories.SharesPostsRepository;
import com.example.socialapplication.repositories.UsersRepository;
import com.example.socialapplication.service.SharesPostService;
import com.example.socialapplication.util.exception.NotFoundException;
import com.example.socialapplication.util.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SharesPostServiceImpl implements SharesPostService {

    private final SharesPostsRepository sharesPostsRepository;
    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;

    public SharesPostServiceImpl(SharesPostsRepository sharesPostsRepository, UsersRepository usersRepository, PostsRepository postsRepository) {
        this.sharesPostsRepository = sharesPostsRepository;
        this.usersRepository = usersRepository;
        this.postsRepository = postsRepository;
    }


    @Override
    public void createdSharePost(String postId, String currentUsername) {
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }

        Optional<Posts> optionalPost = postsRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new NotFoundException("Không tìm thấy bài viết!");
        }

        Posts post = optionalPost.get();

        SharesPosts sharesPosts = new SharesPosts();
        sharesPosts.setPostId(post);
        sharesPosts.setCreateBy(currentUser);

        sharesPostsRepository.save(sharesPosts);
    }

    @Override
    public void deleteSharedPost(String sharesPostId, String currentUsername) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !authentication.getName().equals(currentUsername)) {
            throw new UnauthorizedException("Bạn không có quyền xóa !");
        }

        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }
        Optional<SharesPosts> optionalSavedPost = sharesPostsRepository.findById(sharesPostId);
        if (optionalSavedPost.isEmpty()) {
            throw new NotFoundException("Bài viết không được lưu!");
        }

        SharesPosts sharesPosts = optionalSavedPost.get();
        sharesPostsRepository.delete(sharesPosts);
    }

    @Override
    public List<SharesPosts> getSharedPostByCurrentUser(String currentUsername) {
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }
        return sharesPostsRepository.findByCreateBy(currentUser);
    }

    @Override
    public List<SharesPosts> getSharedPostsByUserId(String userId) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }
        return sharesPostsRepository.findByCreateBy(user);
    }
}
