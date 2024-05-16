package com.example.socialapplication.repositories;

import com.example.socialapplication.model.entity.OTP_ResetPassword;
import com.example.socialapplication.model.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OTP_ResetPasswordRepository extends JpaRepository<OTP_ResetPassword, Long> {

    OTP_ResetPassword findByMailAndOtp(String mail, String otp);
    @Query("SELECT otp FROM OTP_ResetPassword otp JOIN FETCH otp.userId")
    Page<OTP_ResetPassword> findAllWithUser(Pageable pageable);

}