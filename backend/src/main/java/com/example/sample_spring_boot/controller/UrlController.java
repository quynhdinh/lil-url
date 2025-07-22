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
        
        Url url = new Url(request.getOriginalUrl(), shortCode);
        Url savedUrl = urlRepository.save(url);
        
        return ResponseEntity.ok(savedUrl);
    }
    
    @GetMapping("/urls/{shortCode}")
    public ResponseEntity<Url> getUrl(@PathVariable String shortCode) {
        Optional<Url> url = urlRepository.findByShortCode(shortCode);
        
        if (url.isPresent()) {
            return ResponseEntity.ok(url.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    private String generateShortCode() {
        // Simple implementation - in production, use a better algorithm
        return "SC" + System.currentTimeMillis() % 1000000;
    }
    
    // Inner class for request body
    public static class CreateUrlRequest {
        private String originalUrl;
        
        public String getOriginalUrl() {
            return originalUrl;
        }
        
        public void setOriginalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
        }
    }
}
