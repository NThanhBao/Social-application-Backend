package com.example.socialapplication.service.Impl;

import com.example.socialapplication.model.dto.FavoritesDto;
import com.example.socialapplication.model.entity.Medias;
import com.example.socialapplication.model.entity.Posts;
import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.repositories.PostsRepository;
import com.example.socialapplication.repositories.UsersRepository;
import com.example.socialapplication.service.FavoritesService;
import com.example.socialapplication.util.exception.ConflictException;
import com.example.socialapplication.util.exception.NotFoundException;
import com.example.socialapplication.util.exception.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FavoritesServiceImpl implements FavoritesService {

    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;

    public FavoritesServiceImpl(UsersRepository usersRepository, PostsRepository postsRepository) {
        this.usersRepository = usersRepository;
        this.postsRepository = postsRepository;
    }


    @Override
    public void saveFavorite(String postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Bạn cần đăng nhập để thực hiện hành động này!");
        }
        String currentUsername = auth.getName();
        Users user = usersRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new NotFoundException("Không tìm thấy người dùng!");
        }

        Optional<Posts> optionalPost = postsRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new NotFoundException("Không tìm thấy bài đăng có ID: " + postId);
        }

        Posts post = optionalPost.get();

        boolean isPostFavorited = usersRepository.isPostFavoritedByUser(user.getId(), postId);
        if (isPostFavorited) {
            throw new ConflictException("Bài đăng đã được yêu thích trước đó!");
        }

        user.getFavoritesPost().add(post);

        usersRepository.save(user);
    }

    @Override
    public Page<FavoritesDto> getFavoritesByToken(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Users currentUser = usersRepository.findByUsername(currentUsername);
        String currentUserId = currentUser.getId();

        Page<Posts> favoritePostsPage = postsRepository.findFavoritesByUserId(currentUserId, pageable);

        return favoritePostsPage.map(this::convertToDto);
    }

    private FavoritesDto convertToDto(Posts post) {
        FavoritesDto favoritesDto = new FavoritesDto();
        favoritesDto.setPostsID(post.getId());
        favoritesDto.setTitle(post.getTitle());
        favoritesDto.setBody(post.getBody());
        favoritesDto.setAvatar(post.getUserId().getAvatar());
        favoritesDto.setFirstName(post.getUserId().getFirstName());
        favoritesDto.setLastName(post.getUserId().getLastName());
        favoritesDto.setStatus(post.getStatus());
        favoritesDto.setTotalLike(String.valueOf(post.getTotalLike()));
        favoritesDto.setTotalShare(String.valueOf(post.getTotalShare()));
        favoritesDto.setTotalComment(String.valueOf(post.getTotalComment()));
        favoritesDto.setUserID(post.getUserId().getId());
        favoritesDto.setCreateAt(post.getCreateAt());

        // Tạo danh sách chứa các publicUrl của các media
        List<String> mediaPublicUrls = new ArrayList<>();
        if (post.getMedias() != null) {
            for (Medias media : post.getMedias()) {
                mediaPublicUrls.add(media.getPublicUrl());
            }
        }
        favoritesDto.setMediaPublicUrls(mediaPublicUrls);

        return favoritesDto;
    }

    @Override
    public void deleteFavorite(String postId) {
        // Lấy thông tin người dùng hiện tại từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("Bạn phải đăng nhập mới được thực hiện các hành động tiếp theo!");
        }

        // Lấy tên người dùng từ đối tượng Authentication
        String currentUsername = authentication.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        String currentUserId = currentUser.getId();

        // Kiểm tra xem mục yêu thích tồn tại hay không
        int count = postsRepository.countFavoritesByUserIdAndPostId(currentUserId, postId);
        if (count == 0) {
            throw new NotFoundException("Không tìm thấy mục yêu thích!");
        }
        // Xóa mục yêu thích từ bảng favorites
        postsRepository.deleteFavoriteByUserIdAndPostId(currentUserId, postId);
    }

}