package com.url.shortener.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String originalUrl;
    private String shortenUrl;
    private int clickCount = 0;
    private LocalDateTime createdDate;

    // This below annotation means that many url mapping can be done by single user.
    @ManyToOne
    // This specifies the foreign key linking of the user table
    @JoinColumn(name = "user_id")
    private User user;

    // This means the a one url can be click by many times
    @OneToMany(mappedBy = "urlMapping")
    private List<ClickEvent> clickEvents ;
}
