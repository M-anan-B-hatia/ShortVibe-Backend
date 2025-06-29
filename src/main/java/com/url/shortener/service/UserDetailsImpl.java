package com.url.shortener.service;

import com.url.shortener.models.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private long id;
    private String username;
    private String password;
    private String email;

    // Here Granted Authority is the thing which handles the role in Spring Security
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(long id, String username, String password, String email, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
    }

    // This will help me to build the instance of User Details Impl method
    // This method will convert our User Object from our Data Base into a UserDetailsImpl object for Spring Security
    // because Spring Security Will only use this UserDetailsImpl
    public static UserDetailsImpl build(User manualCreatedUser){
        GrantedAuthority authority = new SimpleGrantedAuthority(manualCreatedUser.getRole());
        return new UserDetailsImpl(
                manualCreatedUser.getId(),
                manualCreatedUser.getUserName(),
                manualCreatedUser.getPassword(),
                manualCreatedUser.getEmail(),
                Collections.singletonList(authority)
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}

