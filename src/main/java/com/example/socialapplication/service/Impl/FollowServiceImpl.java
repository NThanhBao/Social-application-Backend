package com.example.socialapplication.service.Impl;

import com.example.socialapplication.model.dto.UsersInfoDto;
import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.repositories.UsersRepository;
import com.example.socialapplication.service.FollowService;
import com.example.socialapplication.util.exception.InvalidRequestException;
import com.example.socialapplication.util.exception.MethodNotAllowedException;
import com.example.socialapplication.util.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {
    private final UsersRepository usersRepository;
    private static final Logger logger = LoggerFactory.getLogger(FollowServiceImpl.class);

    public FollowServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public void followUser(String followingUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Users users = usersRepository.findByUsername(currentUsername);
        Users followingUser = usersRepository.findById(followingUserId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng đang muốn theo dõi."));

        if (users == null || followingUser == null) {
            logger.error("Không tìm thấy người dùng hoặc người dùng đang muốn theo dõi.");
            throw new NotFoundException("Không tìm thấy người dùng hoặc người dùng đang muốn theo dõi.");
        }
        if (users.getId().equals(followingUser.getId())) {
            logger.error("Bạn không thể theo dõi chính mình.");
            throw new MethodNotAllowedException("Bạn không thể theo dõi chính mình.");
        }
        List<Users> following = users.getFollowingUser();
        // Kiểm tra xem users đã theo dõi followingUser chưa
        boolean isAlreadyFollowing = false;
        for (Users user : following) {
            if (user.getId().equals(followingUserId)) {
                isAlreadyFollowing = true;
                break;
            }
        }
        if (!isAlreadyFollowing) {
            following.add(followingUser);
            users.setFollowingUser(following);
            usersRepository.save(users);
            logger.info("Người dùng '{}' đã theo dõi người dùng '{}' thành công.", currentUsername, followingUser.getUsername());
        } else {
            logger.warn("Người dùng '{}' đã theo dõi người dùng '{}' trước đó.", currentUsername, followingUser.getUsername());
            throw new InvalidRequestException("Bạn đã theo dõi người dùng này trước đó rồi.");
        }
    }
    @Override
    public int getFollowingCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Users users = usersRepository.findByUsername(currentUsername);
        int followingCount = users.getFollowingUser().size();
        logger.info("Số lượng người dùng đang theo dõi của '{}' là: {}", currentUsername, followingCount);
        return followingCount;
    }
    @Override
    public int getFollowerCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        int followerCount = usersRepository.countByFollowingUsersUsername(currentUsername);
        logger.info("Số lượng người theo dõi của '{}' là: {}", currentUsername, followerCount);
        return followerCount;
    }
    @Override
    public void unfollowUser(String followingUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Users currentUser = usersRepository.findByUsername(currentUsername);
        Users followingUser = usersRepository.findById(followingUserId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng đang muốn hủy theo dõi."));

        List<Users> following = currentUser.getFollowingUser();
        if (!following.contains(followingUser)) {
            throw new NotFoundException("Bạn đã hủy theo dõi người này trước đó.");
        }
        following.remove(followingUser);
        usersRepository.save(currentUser);
        logger.info("Người dùng '{}' đã hủy theo dõi người dùng '{}'", currentUsername, followingUser.getUsername());
    }
    @Override
    public Page<UsersInfoDto> getFollowingListUsers(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        logger.info("Đang lấy danh sách người dùng đang theo dõi của '{}'", currentUsername);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        String currentUserId = currentUser.getId();
        Page<Users> users = usersRepository.findFollowingUsersByUserId(currentUserId, pageable);

        logger.info("Danh sách người dùng đang theo dõi của '{}' đã được lấy", currentUsername);
        return users.map(this::usersInfoDto);
    }
    @Override
    public Page<UsersInfoDto> getFollowerListUsers(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        logger.info("Đang lấy danh sách người theo dõi của '{}'", currentUsername);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        String currentUserId = currentUser.getId();
        Page<Users> users = usersRepository.findFollowerUsersByUserId(currentUserId, pageable);

        logger.info("Danh sách người theo dõi của '{}' đã được lấy", currentUsername);
        return users.map(this::usersInfoDto);
    }
    @Override
    public int getFollowingCount(String username) {
        logger.info("Đang lấy số người dùng đang theo dõi của '{}'", username);
        Users users = usersRepository.findByUsername(username);
        int followingCount = users.getFollowingUser().size();
        logger.info("Số người dùng đang theo dõi của '{}' đã được lấy: {}", username, followingCount);
        return followingCount;
    }
    @Override
    public int getFollowerCount(String username) {
        logger.info("Đang lấy số người theo dõi của '{}'", username);
        int followerCount = usersRepository.countByFollowingUsersUsername(username);
        logger.info("Số người theo dõi của '{}' đã được lấy: {}", username, followerCount);
        return followerCount;
    }
    private UsersInfoDto usersInfoDto(Users users) {
        UsersInfoDto dto = new UsersInfoDto();
        dto.setId(users.getId());
        dto.setUsername(users.getUsername());
        dto.setFirstName(users.getFirstName());
        dto.setLastName(users.getLastName());
        dto.setAvatar(users.getAvatar());
        dto.setGender(users.isGender());
        dto.setPhoneNumber(users.getPhoneNumber());
        dto.setDateOfBirth(users.getDateOfBirth());
        dto.setMail(users.getMail());
        return dto;
    }

    @Override
    public Page<UsersInfoDto> getUnfollowedUsers(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        logger.info("Đang lấy danh sách người dùng chưa theo dõi của '{}'", currentUsername);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        String currentUserId = currentUser.getId();
        Page<Users> users = usersRepository.findUnfollowedUsersByUserId(currentUserId, pageable);

        logger.info("Danh sách người dùng chưa theo dõi của '{}' đã được lấy", currentUsername);
        return users.map(this::usersInfoDto);
    }
}