package com.example.sample_spring_boot.service;

import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sample_spring_boot.entity.Url;
import com.example.sample_spring_boot.repository.UrlRepository;

@Service
public class ShortenURLService {

    @Autowired
    private UrlRepository urlRepository;

    public String generateShortCode(String originalUrl, Optional<Integer> userId) {
        String shortCode = "SC" + System.currentTimeMillis() % 1000000;
        Optional<Url> byOriginalUrl = urlRepository.findByOriginalUrl(originalUrl);
        if (byOriginalUrl.isPresent()) {
            return byOriginalUrl.get().getShortCode();
        }
        Url newUrl = new Url(originalUrl, shortCode, userId.orElse(-1));
        urlRepository.save(newUrl);
        return shortCode;
    }
}
