package com.example.socialapplication.service.Impl;

import com.example.socialapplication.model.dto.UsersDto;
import com.example.socialapplication.model.dto.UsersInfoDto;
import com.example.socialapplication.model.entity.Enum.EnableType;
import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.service.UsersService;
import com.example.socialapplication.config.CustomUserDetails;
import com.example.socialapplication.repositories.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;


@Service
public class UsersServiceImpl implements UsersService {
    private final UsersRepository registerRepository;
    private final PasswordEncoder encoder;
    private final UsersRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    // Kiểm tra xem mật khẩu có đáp ứng các yêu cầu không
    private boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    public UsersServiceImpl(UsersRepository registerRepository, PasswordEncoder encoder, UsersRepository userRepository) {
        this.registerRepository = registerRepository;
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

//    Lấy danh sách tất cả người dùng.
    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

//    Phương thức load thông tin người dùng theo tên đăng nhập.
    @Override
    public UserDetails loadUserByUsername(String username) {
        Users users = userRepository.findByUsername(username);
        if (users == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(users);
    }

//    Phương thức đăng nhập người dùng.
    @Override
    public UserDetails login(String username, String password) {
        UserDetails userDetails = loadUserByUsername(username);
        if (!encoder.matches(password, userDetails.getPassword())) {
            logger.error("--LOGIN FAILED FOR USER: {}", username);
            throw new BadCredentialsException("Invalid username or password");
        }
        logger.info("--LOGIN SUCCESSFUL FOR USER-- : {}", username);
        return userDetails;
    }

//    Lấy thông tin người dùng theo username.
    @Override
    public Users getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

//    Lấy thông tin người dùng theo ID.
    @Override
    public UsersInfoDto getUserById(String userId) {
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        UsersInfoDto usersDto = new UsersInfoDto();
        usersDto.setId(user.getId());
        usersDto.setUsername(user.getUsername());
        usersDto.setMail(user.getMail());
        usersDto.setGender(user.isGender());
        usersDto.setFirstName(user.getFirstName());
        usersDto.setLastName(user.getLastName());
        usersDto.setAddress(user.getAddress()); 
        usersDto.setDateOfBirth(user.getDateOfBirth());
        usersDto.setPhoneNumber(user.getPhoneNumber());
        usersDto.setAvatar(user.getAvatar());
        usersDto.setBackground(user.getBackground());
        return usersDto;
    }

//    Phương thức thêm người dùng mới.
    @Override
    public ResponseEntity<String> addUser(UsersDto registerDTO) {
        logger.info("Đang thêm người dùng với tên đăng nhập: {}", registerDTO.getUsername());

        if (registerRepository.existsByUsername(registerDTO.getUsername())) {
            logger.warn("Tên người dùng '{}' đã tồn tại", registerDTO.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên người dùng đã tồn tại.");
        }

        if (registerRepository.existsByMail(registerDTO.getMail())) {
            logger.warn("Email '{}' đã được sử dụng", registerDTO.getMail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email đã được sử dụng.");
        }

        if (registerRepository.existsByPhoneNumber(registerDTO.getPhoneNumber())) {
            logger.warn("Số điện thoại '{}' đã được sử dụng", registerDTO.getPhoneNumber());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Số điện thoại đã được sử dụng.");
        }

        // Kiểm tra mật khẩu
        if (!isPasswordValid(registerDTO.getPassword())) {
            logger.warn("Mật khẩu không đáp ứng yêu cầu: mật khẩu phải chứa ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu phải chứa ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.");
        }

        Users user = new Users();

        user.setUsername(registerDTO.getUsername());
        user.setPassword(encoder.encode(registerDTO.getPassword()));
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setGender(registerDTO.isGender());
        user.setPhoneNumber(registerDTO.getPhoneNumber());
        user.setDateOfBirth(registerDTO.getDateOfBirth());
        user.setAddress(registerDTO.getAddress());
        user.setMail(registerDTO.getMail());

        registerRepository.save(user);

        logger.info("Người dùng '{}' đã được tạo thành công", user.getUsername());
        return ResponseEntity.ok("Tạo thành công với tên đăng nhập: " + user.getUsername());
    }

//    Phương thức cập nhật thông tin người dùng.
    @Override
    public ResponseEntity<String> updateUser(UsersDto updatedUserDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users users = userRepository.findByUsername(currentUsername);

        if (!users.getUsername().equals(updatedUserDto.getUsername())) {
            logger.warn("Người dùng '{}' không được phép sửa thông tin người dùng khác.", currentUsername);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền sửa đổi thông tin người dùng khác.");
        }

        Users existingUser = userRepository.findByUsername(updatedUserDto.getUsername());

        existingUser.setFirstName(updatedUserDto.getFirstName());
        existingUser.setLastName(updatedUserDto.getLastName());
        existingUser.setGender(updatedUserDto.isGender());
        existingUser.setPhoneNumber(updatedUserDto.getPhoneNumber());
        existingUser.setDateOfBirth(updatedUserDto.getDateOfBirth());
        existingUser.setAddress(updatedUserDto.getAddress());
        existingUser.setMail(updatedUserDto.getMail());

        registerRepository.save(existingUser);

        logger.info("Thông tin của người dùng '{}' đã được cập nhật thành công", existingUser.getUsername());
        return ResponseEntity.ok("Cập nhật thành công usernames : " + existingUser.getUsername());
    }

//    Phương thức xóa người dùng.
    @Override
    public ResponseEntity<String> deleteUser(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        Users users = userRepository.findByUsername(currentUsername);
        Users userDelete = userRepository.findByUsername(username);

        if (!users.getUsername().equals(username)) {
            logger.warn("Người dùng '{}' không được phép xóa người dùng '{}'", currentUsername, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bạn không có quyền xóa người dùng khác.");
        }

        userDelete.setEnableType(EnableType.FALSE);
        userRepository.save(userDelete);
        logger.info("Người dùng '{}' đã được xóa thành công", username);
        return ResponseEntity.ok("Bạn đã xóa thành công tài khoản của mình.");
    }

//    Phương thức cập nhật mật khẩu của người dùng.
    @Override
    public ResponseEntity<String> updatePassword(String email, String newPassword) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            logger.warn("Người dùng với email '{}' không tồn tại", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại.");
        }
        String encryptedPassword = encoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        userRepository.save(user);
        logger.info("Mật khẩu của người dùng với email '{}' đã được cập nhật thành công", email);
        return ResponseEntity.ok("Mật khẩu cập nhật thành công.");
    }
}