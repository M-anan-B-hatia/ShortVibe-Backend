package com.url.shortener.service;


import com.url.shortener.dtos.ClickEventDto;
import com.url.shortener.dtos.UrlMappingDto;
import com.url.shortener.models.ClickEvent;
import com.url.shortener.models.UrlMapping;
import com.url.shortener.models.User;
import com.url.shortener.repository.ClickEventRepository;
import com.url.shortener.repository.UrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UrlMappingService {

    private UrlMappingRepository urlMappingRepository;
    private ClickEventRepository clickEventRepository;
    // Logic for creating the Short URL
    public UrlMappingDto createShortUrl(String originalUrl, User user) {

        String shortUrl = generateShortUrl();

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setUser(user);
        urlMapping.setCreatedDate(LocalDateTime.now());
        urlMapping.setShortenUrl(shortUrl);

        urlMappingRepository.save(urlMapping);

        return convertToDto(urlMapping);


    }

    private UrlMappingDto convertToDto(UrlMapping urlMapping){
        UrlMappingDto urlMappingDto = new UrlMappingDto();

        urlMappingDto.setId(urlMapping.getId());
        urlMappingDto.setOriginalUrl(urlMapping.getOriginalUrl());
        urlMappingDto.setShortUrl(urlMapping.getShortenUrl());
        urlMappingDto.setClickCount(urlMapping.getClickCount());
        urlMappingDto.setCreatedDate(urlMapping.getCreatedDate());
        urlMappingDto.setUserName(urlMapping.getUser().getUserName());

        return urlMappingDto;
    }


    private String generateShortUrl() {
        Random random = new Random();
        String allChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder shortUrl = new StringBuilder(8);

        for(int i=0; i<8; i++){
            shortUrl.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        return shortUrl.toString();

    }

    public List<UrlMappingDto> getUrls(User user) {
        return urlMappingRepository.findByUser(user).stream()
                .map(this :: convertToDto)
                .toList();

    }

    public List<ClickEventDto> getClickEventsByDate(String shortUrl, LocalDateTime start, LocalDateTime end) {
            UrlMapping urlMapping = urlMappingRepository.findByShortenUrl(shortUrl);

            if(urlMapping != null){
                return clickEventRepository.findByUrlMappingAndClickedDateBetween(urlMapping, start, end)
                        .stream().collect(Collectors.groupingBy(
                                click -> click.getClickedDate().toLocalDate(), Collectors.counting()))
                        .entrySet().stream().map(entry -> {
                            ClickEventDto clickEventDto = new ClickEventDto();
                            clickEventDto.setClickDate(entry.getKey());
                            clickEventDto.setCount(entry.getValue());

                            return clickEventDto;
                        })
                        .collect(Collectors.toList());

            }
            return null;
    }

    public Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDate start, LocalDate end) {

        List<UrlMapping> urlMappings = urlMappingRepository.findByUser(user);

        List<ClickEvent> clickEvents = clickEventRepository.findByUrlMappingInAndClickedDateBetween(urlMappings,
                start.atStartOfDay(),
                end.plusDays(1).atStartOfDay());

        return clickEvents.stream().collect(Collectors.groupingBy(
                click -> click.getClickedDate().toLocalDate(), Collectors.counting()));
    }

    public UrlMapping getOriginalUrl(String shortUrl) {

        UrlMapping urlMapping = urlMappingRepository.findByShortenUrl(shortUrl);

        if( urlMapping != null){
            urlMapping.setClickCount(urlMapping.getClickCount() + 1);
            urlMappingRepository.save(urlMapping);

            // Record the click count s in click event table also

            ClickEvent clickEvent = new ClickEvent();
            clickEvent.setClickedDate(LocalDateTime.now());
            clickEvent.setUrlMapping(urlMapping);

            clickEventRepository.save(clickEvent);


        }

        return urlMapping;
    }
}
