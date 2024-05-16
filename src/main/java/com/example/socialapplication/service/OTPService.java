package com.example.socialapplication.service;

import com.example.socialapplication.model.entity.OTP_ResetPassword;
import com.example.socialapplication.model.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;


public interface OTPService {

    void saveOTP(Users user, String mail, String otp, Timestamp expirationTime);

    String generateOTP();

    String generateOTPAndSendEmail(String email);

    ResponseEntity<String> validateOTP(String mail, String otp);

    Page<OTP_ResetPassword> findAllUsersWithOTP(Pageable pageable);

}
