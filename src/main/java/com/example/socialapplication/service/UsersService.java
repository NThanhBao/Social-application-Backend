package com.example.socialapplication.service;

import com.example.socialapplication.model.dto.UsersDto;
import com.example.socialapplication.model.dto.UsersInfoDto;
import com.example.socialapplication.model.entity.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UsersService extends UserDetailsService {
    UserDetails loadUserByUsername(String username);
    UserDetails login(String username, String password);
    Users getUserByUsername(String username);
    ResponseEntity<String> addUser(UsersDto registerDTO);
    ResponseEntity<String> updateUser(UsersDto updatedUserDto);
    ResponseEntity<String> deleteUser(String username);
    ResponseEntity<String> updatePassword(String email, String newPassword);
    UsersInfoDto getUserById(String userId);
    List<Users> getAllUsers();
}
