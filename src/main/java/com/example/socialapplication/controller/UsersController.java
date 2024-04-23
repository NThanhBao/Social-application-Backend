package com.example.socialapplication.controller;

import com.example.socialapplication.model.dto.SearchUserDto;
import com.example.socialapplication.model.dto.UsersDto;
import com.example.socialapplication.service.Impl.UsersServiceImpl;
import com.example.socialapplication.service.SearchUsersService;
import com.example.socialapplication.util.annotation.CheckLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.socialapplication.config.JwtTokenUtil;
import com.example.socialapplication.model.dto.UsersInfoDto;
import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.service.UsersService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/auth")
public class UsersController {
    private final UsersService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ModelMapper modelMapper;
    private final UsersServiceImpl registerService;
    private final SearchUsersService searchUsersService;
    public UsersController(UsersService userService, JwtTokenUtil jwtTokenUtil, ModelMapper modelMapper, UsersServiceImpl registerService, SearchUsersService searchUsersService) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.modelMapper = modelMapper;
        this.registerService = registerService;
        this.searchUsersService = searchUsersService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        try {
            UserDetails userDetails = userService.login(username, password);
            String token = jwtTokenUtil.generateToken(username);
            Users users = userService.getUserByUsername(username);
            UsersInfoDto usersInfoDto = modelMapper.map(users, UsersInfoDto.class);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("token", token); // Remove colon after "token"
            responseData.put("userInfo", usersInfoDto);

            return ResponseEntity.ok(responseData);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
    @CheckLogin
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users users = userService.getUserByUsername(username);
        if (users == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        UsersInfoDto usersInfoDto = modelMapper.map(users, UsersInfoDto.class);
        return ResponseEntity.ok(usersInfoDto);
    }

    @PostMapping("/register")
    public ResponseEntity<?> addNewUser(@RequestBody UsersDto registerDTO) {
        return registerService.addUser(registerDTO);
    }

//    @CheckLogin
//    @GetMapping("/checktoken")
//    public ResponseEntity<String> protectedApi(HttpServletRequest request) {
//        return ResponseEntity.ok("chỉ khi JWT đúng thì mới xem được thông tin này");
//    }

    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        UsersInfoDto userDTO = userService.getUserById(userId);
        if (userDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDTO);
    }


    @CheckLogin
    @GetMapping("/search")
    public ResponseEntity<List<SearchUserDto>> searchUsersByFullName(@RequestParam String fullName,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "100") int pageSize,
                                                                     @RequestParam(defaultValue = "createAt") String sortName,
                                                                     @RequestParam(defaultValue = "DESC") String sortType) {
        try {
            Sort.Direction direction;
            if (sortType.equalsIgnoreCase("ASC")) {
                direction = Sort.Direction.ASC;
            } else {
                direction = Sort.Direction.DESC;
            }
            Pageable sortedByName = PageRequest.of(page, pageSize, Sort.by(direction, sortName));
            Page<SearchUserDto> usersPage = searchUsersService.findByFullNameContaining(fullName, sortedByName);
            return ResponseEntity.ok().body(usersPage.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @CheckLogin
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UsersDto updatedUserDto) {
        return userService.updateUser(updatedUserDto);
    }

    @CheckLogin
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String username) {
        ResponseEntity<String> response;
        response = userService.deleteUser(username);
        return response;
    }

}

