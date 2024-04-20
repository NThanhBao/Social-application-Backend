package com.example.socialapplication.controller;

import com.example.socialapplication.repositories.UsersRepository;
import com.example.socialapplication.service.OTPService;
import com.example.socialapplication.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/reset-password")
public class ResetPasswordController {
    private final OTPService otpService;
    private final UsersService usersService;
    private final UsersRepository usersRepository;

    @Autowired
    public ResetPasswordController(OTPService otpService, UsersRepository usersRepository, UsersService usersService) {
        this.otpService = otpService;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    @PostMapping("/send-mail")
    public ResponseEntity<String> sendOTP(@RequestParam String email) {
        if (!usersRepository.existsByMail(email)) {
            return new ResponseEntity<>("Email does not exist in the system.", HttpStatus.BAD_REQUEST);
        }
        String otp = otpService.generateOTPAndSendEmail(email);
        return new ResponseEntity<>("OTP sent successfully : " + otp, HttpStatus.OK);
    }

    @PostMapping("/confirm-password")
    public ResponseEntity<String> confirmOTP(@RequestParam String mail,
                                             @RequestParam String otp,
                                             @RequestParam String newPassword) {
        ResponseEntity<String> validationResponse = otpService.validateOTP(mail, otp);
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            return validationResponse;
        }
        ResponseEntity<String> response;
        response = usersService.updatePassword(mail, newPassword);
        return response;
    }
}
