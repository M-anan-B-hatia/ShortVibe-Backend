package com.url.shortener.controller;

import com.url.shortener.dtos.LoginUser;
import com.url.shortener.dtos.RegisterUser;
import com.url.shortener.dtos.SendOtpDto;
import com.url.shortener.dtos.UpdatePasswordDto;
import com.url.shortener.models.User;
import com.url.shortener.repository.UserRepository;
import com.url.shortener.service.OtpService;
import com.url.shortener.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/api/auth")
@RestController
@AllArgsConstructor
public class AuthController {

    private UserService userService;
    private OtpService otpService;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;


    // Registering the User
    @PostMapping("/public/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUser registerUser){
        User user = new User();
        user.setUserName(registerUser.getUserName());
        user.setEmail(registerUser.getEmail());
        user.setPassword(registerUser.getPassword());
        user.setRole("ROLE_USER");

        userService.registerUser(user);

        return ResponseEntity.ok("User Register Successfully");

    }

    // Login the user
    @PostMapping("/public/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginUser loginUserRequest){
        return ResponseEntity.ok(userService.authenticateUser(loginUserRequest));
    }

    //Send OTP
    @PostMapping("/public/sendOtp")
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpDto sendOtpDto){

        Optional<User> optionalUser = userRepository.findByEmail(sendOtpDto.getEmail());
        if(optionalUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with this email does not exist");
        }

        try {
            otpService.sendOtp(sendOtpDto.getEmail());
            return ResponseEntity.ok("OTP sent successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP: " + e.getMessage());
        }

    }


    @PutMapping("/public/resetPassword")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto){
        if( otpService.verifyOtp(updatePasswordDto.getEmail(), updatePasswordDto.getOtp()) ){
            User user = userRepository.findByEmail(updatePasswordDto.getEmail()).
                    orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + updatePasswordDto.getEmail()));

            user.setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
            userRepository.save(user);

            return ResponseEntity.ok("Password Updated Successfully");

        }

        return ResponseEntity.badRequest().body("Invalid OTP or Email");
    }



}
