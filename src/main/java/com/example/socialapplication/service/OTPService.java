package com.example.socialapplication.service;

import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;


public interface OTPService {

    void saveOTP(String email, String otp, Timestamp expirationTime);

    String generateOTP();

    String generateOTPAndSendEmail(String email);

    ResponseEntity<String> validateOTP(String mail, String otp);

}
