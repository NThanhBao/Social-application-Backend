package com.example.socialapplication.service.Impl;

import com.example.socialapplication.model.dto.SharesPostDto;
import com.example.socialapplication.model.entity.Posts;
import com.example.socialapplication.model.entity.SharesPosts;
import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.repositories.PostsRepository;
import com.example.socialapplication.repositories.SharesPostsRepository;
import com.example.socialapplication.repositories.UsersRepository;
import com.example.socialapplication.service.SharesPostService;
import com.example.socialapplication.util.exception.NotFoundException;
import com.example.socialapplication.util.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class SharesPostServiceImpl implements SharesPostService {

    private final SharesPostsRepository sharesPostsRepository;
    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;
    private static final Logger logger = LoggerFactory.getLogger(CommentsServiceImpl.class);

    public SharesPostServiceImpl(SharesPostsRepository sharesPostsRepository, UsersRepository usersRepository, PostsRepository postsRepository) {
        this.sharesPostsRepository = sharesPostsRepository;
        this.usersRepository = usersRepository;
        this.postsRepository = postsRepository;
    }

    @Override
    public SharesPosts createdSharePost(SharesPostDto sharesPostDto) {
        String postId = sharesPostDto.getPostId();
        String currentUsername = sharesPostDto.getCreateBy();

        Optional<Posts> optionalPost = postsRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new NotFoundException("Không tìm thấy bài viết!");
        }

        Posts post = optionalPost.get();
        post.setTotalShare(post.getTotalShare() + 1);
        postsRepository.save(post);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }

        SharesPosts sharesPosts = new SharesPosts();
        sharesPosts.setCreateBy(currentUser);
        sharesPosts.setPostId(post);
        sharesPosts.setCreateAt(new Timestamp(System.currentTimeMillis()));

        logger.info("Share thành công bài viết với ID: {}", postId);
        return sharesPostsRepository.save(sharesPosts);
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
        logger.info("Xóa thành công bài viết đã Chia sẻ.");
        sharesPostsRepository.delete(sharesPosts);
    }

    @Override
    public List<SharesPosts> getSharedPostByCurrentUser(String currentUsername) {
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }
        logger.info("Lấy thành công bài viết cho user đã đăng nhập.");
        return sharesPostsRepository.findByCreateBy(currentUser);
    }

    @Override
    public List<SharesPosts> getSharedPostsByUserId(String userId) {
        Users user = usersRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }
        logger.error("lấy thành công cho user có id: {}", userId);
        return sharesPostsRepository.findByCreateBy(user);
    }
}
