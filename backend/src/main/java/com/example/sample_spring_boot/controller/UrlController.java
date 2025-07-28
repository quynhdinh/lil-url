package com.example.sample_spring_boot.controller;

import com.example.sample_spring_boot.entity.Url;
import com.example.sample_spring_boot.repository.UrlRepository;
import com.example.sample_spring_boot.service.ShortenURLService;
import com.example.sample_spring_boot.service.ClickService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UrlController {
    
    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private ShortenURLService shortenURLService;

    @Autowired
    private ClickService clickService;

    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Application is running and database connection is active!");
    }
    
    @GetMapping("/urls")
    public ResponseEntity<List<Url>> getAllUrls() {
        List<Url> urls = urlRepository.findAll();
        return ResponseEntity.ok(urls);
    }
    
    @GetMapping("/urls/{shortCode}")
    public ResponseEntity<Url> getUrl(@PathVariable String shortCode) {
        Optional<Url> url = urlRepository.findByShortCode(shortCode);
        if (url.isPresent()) {
            clickService.recordClick(shortCode, url.get().getUserId()); // Record click for anonymous user
            return ResponseEntity.ok(url.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@RequestBody ShortenUrlRequest request) {
        Optional<Integer> userId = Optional.ofNullable(request.getUserId());
        String originalUrl = request.getUrl();
        // Generate a short code for the URL
        String shortCode = shortenURLService.generateShortCode(request.getUrl(), userId);
        
        // Create response object
        ShortenUrlResponse response = new ShortenUrlResponse();
        response.setOriginalUrl(originalUrl);
        response.setShortCode(shortCode);
        response.setUserId(userId.orElse(-1));
        response.setMessage("URL shortened successfully");
        
        return ResponseEntity.ok(response);
    }
    
    // Inner class for request body
    public static class CreateUrlRequest {
        private String originalUrl;
        private Integer userId;
        
        public String getOriginalUrl() {
            return originalUrl;
        }
        
        public void setOriginalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
        }
        
        public Integer getUserId() {
            return userId;
        }
        
        public void setUserId(Integer userId) {
            this.userId = userId;
        }
    }
    
    // Inner class for shorten URL request body
    public static class ShortenUrlRequest {
        private String url;
        private Integer userId;
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public Integer getUserId() {
            return userId;
        }
        
        public void setUserId(Integer userId) {
            this.userId = userId;
        }
        
        @Override
        public String toString() {
            return "ShortenUrlRequest{" +
                    "url='" + url + '\'' +
                    ", userId=" + userId +
                    '}';
        }
    }
    
    // Inner class for shorten URL response body
    public static class ShortenUrlResponse {
        private String originalUrl;
        private String shortCode;
        private Integer userId;
        private String message;
        
        public String getOriginalUrl() {
            return originalUrl;
        }
        
        public void setOriginalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
        }
        
        public String getShortCode() {
            return shortCode;
        }
        
        public void setShortCode(String shortCode) {
            this.shortCode = shortCode;
        }
        
        public Integer getUserId() {
            return userId;
        }
        
        public void setUserId(Integer userId) {
            this.userId = userId;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    // Inner class for get URL response body
    public static class GetUrlResponse {
        private String originalUrl;
        private String shortCode;
        private String message;
        
        public String getOriginalUrl() {
            return originalUrl;
        }
        
        public void setOriginalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
        }
        
        public String getShortCode() {
            return shortCode;
        }
        
        public void setShortCode(String shortCode) {
            this.shortCode = shortCode;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
