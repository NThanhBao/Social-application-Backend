package com.example.socialapplication.config;

import com.example.socialapplication.model.entity.Users;
import com.example.socialapplication.repositories.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Autowired
    private UsersRepository usersRepository;

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        Users user = usersRepository.findByUsername(username);
        if (user != null) {
            claims.put("userId", user.getId());
            claims.put("role", user.getRoleType());
        } else {
            throw new RuntimeException("User not found");
        }
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()));
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }
    public String extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", String.class);
    }
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //Kiểm tra hạn của token
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);

            Date expirationDate = claims.getExpiration();

            return expirationDate != null && expirationDate.before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }
    // Kiểm tra tính hợp lệ của token
    public boolean isTokenValid(String token) {
        try {
            String userId = extractUserId(token);
            Users user = usersRepository.findById(userId).orElse(null);
            return user != null;
        } catch (Exception e) {
            return false;
        }
    }

    //Kiêm tra đinhhj dạng
    public Boolean isTokenFormatValid(String token) {
        // Độ dài tối thiểu và tối đa của token
        final int MIN_TOKEN_LENGTH = 10;
        final int MAX_TOKEN_LENGTH = 1000;

        int tokenLength = token.length();
        if (tokenLength < MIN_TOKEN_LENGTH || tokenLength > MAX_TOKEN_LENGTH) {
            return false;
        }
        if (!token.matches("[a-zA-Z0-9._-]+")) {
            return false;
        }
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }
}
