package com.url.shortener.dtos;

import lombok.Data;

import java.util.Set;

@Data
public class RegisterUser {
    private String email;
    private String userName;
    private String password;
    private Set<String> roles;
}
