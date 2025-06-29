package com.url.shortener.dtos;

import lombok.Data;

@Data
public class UpdatePasswordDto {

    private String email;
    private String otp;
    private String newPassword;
}
