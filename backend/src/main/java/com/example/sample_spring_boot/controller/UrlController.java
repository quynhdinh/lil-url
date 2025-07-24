package com.example.sample_spring_boot.controller;

import com.example.sample_spring_boot.entity.Url;
import com.example.sample_spring_boot.repository.UrlRepository;
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
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Application is running and database connection is active!");
    }
    
    @GetMapping("/urls")
    public ResponseEntity<List<Url>> getAllUrls() {
        List<Url> urls = urlRepository.findAll();
        return ResponseEntity.ok(urls);
    }
    
    @PostMapping("/urls")
    public ResponseEntity<Url> createUrl(@RequestBody CreateUrlRequest request) {
        // Generate a simple short code (in production, use a proper algorithm)
        String shortCode = generateShortCode();
        
        Url url = new Url(request.getOriginalUrl(), shortCode, request.getUserId());
        Url savedUrl = urlRepository.save(url);
        
        return ResponseEntity.ok(savedUrl);
    }
    
    @GetMapping("/urls/{shortCode}")
    public ResponseEntity<Url> getUrl(@PathVariable String shortCode) {
        Optional<Url> url = urlRepository.findByShortCode(shortCode);
        // System.out.println("here");
        if (url.isPresent()) {
            return ResponseEntity.ok(url.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@RequestBody ShortenUrlRequest request) {
        // Print the JSON body to console
        System.out.println("Received shorten request:");
        System.out.println("URL: " + request.getUrl());
        System.out.println("User ID: " + request.getUserId());
        System.out.println("Full request: " + request.toString());
        Optional<Integer> userId = Optional.ofNullable(request.getUserId());
        
        // Generate a short code for the URL
        String shortCode = generateShortCode();
        
        // Save to database
        Url url = new Url(request.getUrl(), shortCode, userId.orElse(-1));
        Url savedUrl = urlRepository.save(url);
        
        // Create response object
        ShortenUrlResponse response = new ShortenUrlResponse();
        response.setOriginalUrl(savedUrl.getOriginalUrl());
        response.setShortCode(savedUrl.getShortCode());
        response.setUserId(savedUrl.getUserId());
        response.setMessage("URL shortened successfully");
        
        return ResponseEntity.ok(response);
    }
    
    private String generateShortCode() {
        // Simple implementation - in production, use a better algorithm
        return "SC" + System.currentTimeMillis() % 1000000;
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
