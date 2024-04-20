package com.example.socialapplication.model.dto;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class OTP_ResetPasswordDto {
    private String mail;
    private String otp;
    private Timestamp expirationTime;
}
