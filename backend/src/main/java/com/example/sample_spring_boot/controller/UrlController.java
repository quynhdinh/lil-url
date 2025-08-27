package com.example.sample_spring_boot.controller;

import com.example.sample_spring_boot.entity.Url;
import com.example.sample_spring_boot.service.ShortenURLService;
import com.example.sample_spring_boot.service.UrlService;
import com.example.sample_spring_boot.service.ClickService;
import com.example.sample_spring_boot.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/*
 * a signed-in user can create a custom short URL
 * a signed-in user can view all URLs they created
*/
@RestController
@RequestMapping("/api")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

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
        List<Url> urls = urlService.findAll();
        return ResponseEntity.ok(urls);
    }

    @GetMapping("/urls/{shortCode}")
    public ResponseEntity<Url> getUrl(@PathVariable String shortCode) {
        Optional<Url> url = urlService.findByShortCode(shortCode);
        if (url.isPresent()) {
            clickService.recordClick(shortCode, url.get().getUserId()); // Record click for anonymous user
            return ResponseEntity.ok(url.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users/{userId}/urls")
    public ResponseEntity<List<Url>> getMyUrls() {
        // Get the authenticated user's ID from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Integer userId = user.getId();

        List<Url> userUrls = urlService.findByUserId(userId);
        return ResponseEntity.ok(userUrls);
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@RequestBody ShortenUrlRequest request) {
        // Get authenticated a user if available
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Integer> userId = Optional.empty();
        
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            userId = Optional.of(((User) authentication.getPrincipal()).getId());
        }
        
        String originalUrl = request.url();
        if (userId.isEmpty() && request.customCode() != null) {
            ShortenUrlResponse response = new ShortenUrlResponse(null, null, null, "Custom short code is not allowed for anonymous users");
            return ResponseEntity.badRequest().body(response);
        }
        if (userId.isPresent() && urlService.isShortCodeExists(request.customCode())) {
            ShortenUrlResponse response = new ShortenUrlResponse(null, null, null, "Custom short code already exists. Please choose another one.");
            return ResponseEntity.badRequest().body(response);
        }
        String shortCode = shortenURLService.generateShortCode(request.url(), Optional.ofNullable(request.customCode()), userId);

        // Create response object
        ShortenUrlResponse response = new ShortenUrlResponse(originalUrl, shortCode, userId.orElse(-1), "URL shortened successfully");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/urls")
    public ResponseEntity<String> deleteUrl(@RequestParam String shortCode) {
        // Get authenticated user ID

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Integer> userId = Optional.empty();

        if (authentication != null && authentication.getPrincipal() instanceof User) {
            userId = Optional.of(((User) authentication.getPrincipal()).getId());
        }
        if (userId.isEmpty()){
            return ResponseEntity.badRequest().body("User is not authenticated. Please sign in first.");
        }

        // Check if URL exists and belongs to the user
        Optional<Url> urlOptional = urlService.findByShortCodeAndUserId(shortCode, userId.get());
        if (urlOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Delete the URL
        urlService.delete(urlOptional.get());
        return ResponseEntity.ok("URL deleted successfully");
    }

    // Inner class for request body
    public record CreateUrlRequest(String originalUrl, Integer userId) {
    }

    public record ShortenUrlRequest(String url, String customCode) {
    }

    // Inner class for shortening URL response body
    public record ShortenUrlResponse(String originalUrl, String shortCode, Integer userId, String message) {
    }

    // Inner class for get URL response body
    public record GetUrlResponse(String originalUrl, String shortCode, String message) {
    }
}
