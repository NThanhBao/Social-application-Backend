package com.example.socialapplication.service;

import com.example.socialapplication.model.dto.SearchUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchUsersService {
    Page<SearchUserDto> findByFullNameContaining(String fullName, Pageable pageable);
}
