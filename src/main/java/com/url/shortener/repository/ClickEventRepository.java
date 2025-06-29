package com.url.shortener.repository;

import com.url.shortener.models.ClickEvent;
import com.url.shortener.models.UrlMapping;
import com.url.shortener.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    List<ClickEvent> findByUrlMappingAndClickedDateBetween(UrlMapping urlMapping,
                                                           LocalDateTime startDate,
                                                           LocalDateTime endDate);



    // This Method will help to get total clicks count for all urls for that particular user.
    List<ClickEvent> findByUrlMappingInAndClickedDateBetween(List<UrlMapping> urlMappings,
                                                           LocalDateTime startDate,
                                                           LocalDateTime endDate);
}

