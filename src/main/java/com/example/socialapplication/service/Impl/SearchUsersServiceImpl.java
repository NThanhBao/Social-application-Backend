package com.example.socialapplication.service.Impl;

import com.example.socialapplication.model.dto.SearchUserDto;
import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.repositories.UsersRepository;
import com.example.socialapplication.service.SearchUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SearchUsersServiceImpl implements SearchUsersService {

    private final UsersRepository searchRepository;
    private static final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);

    public SearchUsersServiceImpl(UsersRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public Page<SearchUserDto> findByFullNameContaining(String fullName, Pageable pageable) {
        logger.info("Đang tìm kiếm người dùng theo tên đầy đủ : '{}'", fullName);

        Page<Users> usersPage = searchRepository.findByFullNameAndNotAdminRole(fullName, pageable);

        logger.info("Đã tìm thấy {} người dùng theo tên đầy đủ : '{}'", usersPage.getTotalElements(), fullName);

        return usersPage.map(this::convertToDTO);
    }

    private SearchUserDto convertToDTO(Users user) {
        SearchUserDto dto = new SearchUserDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setMail(user.getMail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setGender(user.isGender());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setAddress(user.getAddress());
        dto.setAvatar(user.getAvatar());
        dto.setBackground(user.getBackground());
        return dto;
    }
}