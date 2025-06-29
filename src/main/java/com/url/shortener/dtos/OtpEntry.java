package com.url.shortener.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OtpEntry {

    private final String otp;
    private final LocalDateTime timeStamp;

    public OtpEntry(String otp, LocalDateTime timeStamp) {
        this.otp = otp;
        this.timeStamp = timeStamp;
    }
}
