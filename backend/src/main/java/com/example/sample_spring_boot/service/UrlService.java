package com.example.sample_spring_boot.service;

import org.springframework.stereotype.Service;

import com.example.sample_spring_boot.entity.Url;
import com.example.sample_spring_boot.repository.UrlRepository;
import java.util.Optional;
import java.util.List;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public List<Url> findByUserId(Integer userId) {
        return urlRepository.findByUserId(userId);
    }

    public boolean isShortCodeExists(String shortCode) {
        return urlRepository.existsByShortCode(shortCode);
    }

    public List<Url> findAll() {
        return urlRepository.findAll();
    }

    public Optional<Url> findByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode);
    }

    public Optional<Url> findByShortCodeAndUserId(String shortCode, Integer userId) {
        return urlRepository.findByShortCodeAndUserId(shortCode, userId);
    }

    public void delete(Url url) {
        urlRepository.delete(url);
    }

    public long countByUserId(Integer userId) {
        return urlRepository.countByUserId(userId);
    }
}
