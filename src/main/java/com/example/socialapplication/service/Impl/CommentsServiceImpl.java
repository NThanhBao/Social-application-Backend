package com.example.socialapplication.service.Impl;

import com.example.socialapplication.model.dto.CommentsDto;
import com.example.socialapplication.model.entity.Comments;
import com.example.socialapplication.model.entity.Posts;
import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.repositories.CommentsRepository;
import com.example.socialapplication.repositories.PostsRepository;
import com.example.socialapplication.repositories.UsersRepository;
import com.example.socialapplication.service.CommentsService;
import com.example.socialapplication.util.exception.NotFoundException;
import com.example.socialapplication.util.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommentsServiceImpl implements CommentsService {
    private final UsersRepository usersRepository;
    private final CommentsRepository commentsRepository;
    private final PostsRepository postsRepository;
    private static final Logger logger = LoggerFactory.getLogger(CommentsServiceImpl.class);

    public CommentsServiceImpl(UsersRepository usersRepository, CommentsRepository commentsRepository, PostsRepository postsRepository) {
        this.usersRepository = usersRepository;
        this.commentsRepository = commentsRepository;
        this.postsRepository = postsRepository;
    }

    @Override
    public Comments saveComment(CommentsDto commentsDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null ) {
            throw new NotFoundException("bạn cần đăng nhập để thực hiện hành động này !");
        }

        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }

        Optional<Posts> post = postsRepository.findById(commentsDto.getPostId());
        if (post .isEmpty()) {
            throw new NotFoundException("Không tìm thấy bài viết!");
        }

        Posts posts = post.get();
        posts.setTotalComment(posts.getTotalComment() + 1);

        Comments comments = new Comments();
        comments.setContent(commentsDto.getContent());
        comments.setCreateBy(currentUser);
        comments.setPostId(post.get());
        comments.setCreateAt(new Timestamp(System.currentTimeMillis()));
        postsRepository.save(posts);
        logger.info("Thêm thành công comment.");
        return commentsRepository.save(comments);
    }

    @Override
    public Page<Comments> getCommentsByPostId(String postId, Pageable pageable) {
        return commentsRepository.findByPostId(postId, pageable);
    }

    @Override
    public void deleteComment(UUID commentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null ) {
            throw new NotFoundException("bạn cần đăng nhập để thực hiện hành động này !");
        }

        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }

        Optional<Comments> comments = commentsRepository.findById(commentId.toString());
        if (comments.isEmpty()){
            throw new NotFoundException("không tìm thấy bài viết cần xóa !");
        }
        Comments comment = comments.get();
        if (!comment.getCreateBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Bạn không có quyền xóa bình luận này!");
        }
        Posts posts = comment.getPostId();
        posts.setTotalComment(posts.getTotalComment() - 1);

        postsRepository.save(posts);
        logger.info("Xóa thành công comment.");
        commentsRepository.deleteById(commentId.toString());
    }

    @Override
    public void updateComments(CommentsDto commentsDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null ) {
            throw new NotFoundException("bạn cần đăng nhập để thực hiện hành động này !");
        }

        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }


        Comments comment = commentsRepository.findById(commentsDto.getId()).orElse(null);
        if (comment == null){
            throw new NotFoundException("Không tìm thấy bình luận cần cập nhật !");
        }

        if (!comment.getCreateBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Bạn không có quyền cập nhật bình luận này!");
        }

        comment.setContent(commentsDto.getContent());
        commentsDto.setCreateAt(new Timestamp(System.currentTimeMillis()));
        logger.info("Sửa thành công comment.");
        commentsRepository.save(comment);
    }
}
