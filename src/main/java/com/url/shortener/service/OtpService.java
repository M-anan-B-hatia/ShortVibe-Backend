package com.url.shortener.service;


import com.url.shortener.dtos.OtpEntry;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class OtpService {

        @Autowired
        private JavaMailSender mailSender;

        private static final int OTP_Expiry_Minutes = 10;
        private static final int RATE_LIMIT_SECONDS = 60;


        // This Map will Store the exact date time of an otp with otp itself and  with respective email
        private final ConcurrentHashMap<String, OtpEntry> otpStore = new ConcurrentHashMap<>();


        // This Map will Store the exact date time of an otp  and  with respective email
        private final ConcurrentHashMap<String, LocalDateTime>  requestTimestamps = new ConcurrentHashMap<>();


        // This will generate the 6 digit otp with leading zeroes added with random number if number is less than 6 digits
        public String generateOtp(){
                return String.format("%06d", new Random().nextInt(999999));
        }


        public void sendOtp(String email){

                LocalDateTime lastRequest = requestTimestamps.get(email);
                if(lastRequest != null && lastRequest.plusSeconds(RATE_LIMIT_SECONDS).isAfter(LocalDateTime.now())){
                        throw new RuntimeException("Please wait before requesting another OTP.");
                }

                String otp = generateOtp();
                LocalDateTime now = LocalDateTime.now();

                otpStore.put(email, new OtpEntry(otp, now));
                requestTimestamps.put(email, now);
//
//                SimpleMailMessage message = new SimpleMailMessage();
//                message.setTo(email);
//                message.setSubject(" Your OTP for Password Reset");
//                message.setText("Dear User,\n\nYour OTP is: " + otp + "\n\nIt will expire in 10 minutes.");
//
//                mailSender.send(message);

                try {
                        MimeMessage mimeMessage  = mailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                        helper.setTo(email);
                        helper.setSubject("OTP Verification E-mail");

                        String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/OTPMail.html")));
                        htmlTemplate = htmlTemplate.replace("${otp}", otp);

                        helper.setText(htmlTemplate, true);

                        ClassPathResource image = new ClassPathResource("static/images/Logo.jpg");
                        helper.addInline("logoImage", image);

                        mailSender.send(mimeMessage);


                } catch (MessagingException  | IOException e) {
                    throw new RuntimeException("Failed to Send OTP E-mail", e);
                }

                System.out.println("otpStoreMap" + otpStore);
                System.out.println("requestTimestamps" + requestTimestamps);



        }

        public boolean verifyOtp(String email, String otp){
            OtpEntry otpEntry = otpStore.get(email);
            if(otpEntry == null){
                    return false;
            }

            return otpEntry.getOtp().equals(otp);

        }






}
