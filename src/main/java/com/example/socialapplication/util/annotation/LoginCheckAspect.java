package com.example.socialapplication.util.annotation;

import com.example.socialapplication.config.JwtTokenUtil;
import com.example.socialapplication.util.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
public class LoginCheckAspect {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Before("@annotation(CheckLogin)")
    public ResponseEntity<?> checkLogin(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Không tìm thấy token.");
            }

            String token = authHeader.substring(7);

            // Kiểm tra định dạng của token
            if (!jwtTokenUtil.isTokenFormatValid(token)) {
                throw new UnauthorizedException("Token không đúng định dạng.");
            }

            // Kiểm tra xem token đã hợp lệ hay không
            if (!jwtTokenUtil.isTokenValid(token)) {
                throw new UnauthorizedException("Token không có thông tin.");
            }

            // Kiểm tra xem token đã hết hạn chưa
            if (jwtTokenUtil.isTokenExpired(token)) {
                throw new UnauthorizedException("Token đã hết hạn");
            }

            // Nếu tất cả các kiểm tra đều thành công, trả về thông tin người dùng
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                String currentUserName = authentication.getName();
                return ResponseEntity.ok(currentUserName);
            }
            throw new UnauthorizedException("Có lỗi xảy ra!");
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException("Có lỗi xảy ra!");
        }
    }
}
