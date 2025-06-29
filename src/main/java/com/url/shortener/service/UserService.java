package com.url.shortener.service;

import com.url.shortener.dtos.LoginUser;
import com.url.shortener.models.User;
import com.url.shortener.repository.UserRepository;
import com.url.shortener.security.jwt.JwtAuthenticationResponse;
import com.url.shortener.security.jwt.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserService {

    private PasswordEncoder passwordEncoder;;
    private JwtUtils jwtUtils;
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;


    public User registerUser(User user){
        if(userRepository.findByEmail(user.getEmail()).isPresent() ){
            throw new RuntimeException("Email is already in use Please try to Login");
        }

        if(userRepository.findByUserName(user.getUserName()).isPresent() ){
            throw new RuntimeException("Username Already Exists try with different username");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public JwtAuthenticationResponse authenticateUser(LoginUser loginUser){

        // This line of code will do the authentication that this particular username and passowrd is valid or not
        // and if it is valid it will give a fully populated user object.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUser.getUserName(), loginUser.getPassword())
        );

        //Hey, this user is authenticated — here's their authentication info.
        // Please treat this user as logged in for the current session/request.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // This will help to make the authenticated user details fill in userDetaisl object
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // This will help to generate the token for authenticated user
        String jwt = jwtUtils.generateToken(userDetails);
        return new JwtAuthenticationResponse(jwt);
    }

    public User findByUserName(String name) {

        return userRepository.findByUserName(name).orElseThrow(
                () -> new UsernameNotFoundException("User not found with given username: " +  name)
        );
    }
}
