package com.example.sample_spring_boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sample_spring_boot.repository.UrlRepository;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    public boolean isShortCodeExists(String shortCode) {
        return urlRepository.existsByShortCode(shortCode);
    }
}
