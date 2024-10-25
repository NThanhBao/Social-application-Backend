package com.example.socialapplication.model.dto;

import com.example.socialapplication.model.entity.Users;
import lombok.Data;

import java.sql.Timestamp;
@Data
public class OTP_ResetPasswordDto {

    private String id;

    private Users userId;

    private String mail;

    private String otp;

    private Timestamp createAt;

    private Timestamp expirationTime;

    @Override
    public String toString() {
        return "OTP_ResetPasswordDto{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", mail='" + mail + '\'' +
                ", otp='" + otp + '\'' +
                ", createAt=" + createAt +
                ", expirationTime=" + expirationTime +
                '}';
    }
}
