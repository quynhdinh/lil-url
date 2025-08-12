package com.example.sample_spring_boot.controller;

import com.example.sample_spring_boot.entity.Url;
import com.example.sample_spring_boot.repository.UrlRepository;
import com.example.sample_spring_boot.service.ClickService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ClickController {
    
    private final ClickService clickService;
    
    private final UrlRepository urlRepository;

    public ClickController(ClickService clickService, UrlRepository urlRepository) {
        this.clickService = clickService;
        this.urlRepository = urlRepository;
    }

    /**
     * Get link count (number of URLs created) by user
     * @param userId The user ID
     * @return Response with link count
     */
    @GetMapping("/users/{userId}/link-count")
    public ResponseEntity<LinkCountResponse> getLinkCountByUser(@PathVariable Integer userId) {
        long linkCount = urlRepository.countByUserId(userId);
        
        LinkCountResponse response = new LinkCountResponse();
        response.setUserId(userId);
        response.setLinkCount(linkCount);
        response.setMessage("Link count retrieved successfully");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get total clicks by user (across all their URLs)
     * @param userId The user ID
     * @return Response with total click count
     */
    @GetMapping("/users/{userId}/total-clicks")
    public ResponseEntity<TotalClicksResponse> getTotalClicksByUser(@PathVariable Integer userId) {
        // Get all URLs created by the user
        List<Url> userUrls = urlRepository.findByUserId(userId);
        
        // Count total clicks across all user's URLs
        long totalClicks = userUrls.stream()
                .mapToLong(url -> clickService.getClickCount(url.getShortCode()))
                .sum();
        
        TotalClicksResponse response = new TotalClicksResponse();
        response.setUserId(userId);
        response.setTotalClicks(totalClicks);
        response.setUrlCount(userUrls.size());
        response.setMessage("Total clicks retrieved successfully");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get comprehensive user statistics (both link count and total clicks)
     * @param userId The user ID
     * @return Response with comprehensive stats
     */
    @GetMapping("/users/{userId}/stats")
    public ResponseEntity<UserStatsResponse> getUserStats(@PathVariable Integer userId) {
        // Get link count
        long linkCount = urlRepository.countByUserId(userId);
        
        // Get all URLs created by the user
        List<Url> userUrls = urlRepository.findByUserId(userId);
        
        // Count total clicks across all user's URLs
        long totalClicks = userUrls.stream()
                .mapToLong(url -> clickService.getClickCount(url.getShortCode()))
                .sum();
        
        // Calculate average clicks per URL
        double averageClicksPerUrl = linkCount > 0 ? (double) totalClicks / linkCount : 0.0;
        
        UserStatsResponse response = new UserStatsResponse();
        response.setUserId(userId);
        response.setLinkCount(linkCount);
        response.setTotalClicks(totalClicks);
        response.setAverageClicksPerUrl(averageClicksPerUrl);
        response.setMessage("User statistics retrieved successfully");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get clicks for a specific URL by user
     * @param userId The user ID
     * @param shortCode The short code of the URL
     * @return Response with URL click statistics
     */
    @GetMapping("/users/{userId}/urls/{shortCode}/clicks")
    public ResponseEntity<UrlClicksResponse> getUrlClicksByUser(@PathVariable Integer userId, 
                                                               @PathVariable String shortCode) {
        // Verify the URL belongs to the user
        Optional<Url> url = urlRepository.findByShortCodeAndUserId(shortCode, userId);
        
        if (url.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Get click statistics for this URL
        ClickService.ClickStats clickStats = clickService.getClickStats(shortCode);
        
        UrlClicksResponse response = new UrlClicksResponse();
        response.setUserId(userId);
        response.setShortCode(shortCode);
        response.setOriginalUrl(clickStats.getOriginalUrl());
        response.setTotalClicks(clickStats.getTotalClicks());
        response.setUniqueUsers(clickStats.getUniqueUsers());
        response.setAnonymousClicks(clickStats.getAnonymousClicks());
        response.setMessage("URL clicks retrieved successfully");
        
        return ResponseEntity.ok(response);
    }
    
    // Response classes
    public static class LinkCountResponse {
        private Integer userId;
        private long linkCount;
        private String message;
        
        // Getters and setters
        public Integer getUserId() {
            return userId;
        }
        
        public void setUserId(Integer userId) {
            this.userId = userId;
        }
        
        public long getLinkCount() {
            return linkCount;
        }
        
        public void setLinkCount(long linkCount) {
            this.linkCount = linkCount;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    public static class TotalClicksResponse {
        private Integer userId;
        private long totalClicks;
        private long urlCount;
        private String message;
        
        // Getters and setters
        public Integer getUserId() {
            return userId;
        }
        
        public void setUserId(Integer userId) {
            this.userId = userId;
        }
        
        public long getTotalClicks() {
            return totalClicks;
        }
        
        public void setTotalClicks(long totalClicks) {
            this.totalClicks = totalClicks;
        }
        
        public long getUrlCount() {
            return urlCount;
        }
        
        public void setUrlCount(long urlCount) {
            this.urlCount = urlCount;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    public static class UserStatsResponse {
        private Integer userId;
        private long linkCount;
        private long totalClicks;
        private double averageClicksPerUrl;
        private String message;
        
        // Getters and setters
        public Integer getUserId() {
            return userId;
        }
        
        public void setUserId(Integer userId) {
            this.userId = userId;
        }
        
        public long getLinkCount() {
            return linkCount;
        }
        
        public void setLinkCount(long linkCount) {
            this.linkCount = linkCount;
        }
        
        public long getTotalClicks() {
            return totalClicks;
        }
        
        public void setTotalClicks(long totalClicks) {
            this.totalClicks = totalClicks;
        }
        
        public double getAverageClicksPerUrl() {
            return averageClicksPerUrl;
        }
        
        public void setAverageClicksPerUrl(double averageClicksPerUrl) {
            this.averageClicksPerUrl = averageClicksPerUrl;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    public static class UrlClicksResponse {
        private Integer userId;
        private String shortCode;
        private String originalUrl;
        private long totalClicks;
        private long uniqueUsers;
        private long anonymousClicks;
        private String message;
        
        // Getters and setters
        public Integer getUserId() {
            return userId;
        }
        
        public void setUserId(Integer userId) {
            this.userId = userId;
        }
        
        public String getShortCode() {
            return shortCode;
        }
        
        public void setShortCode(String shortCode) {
            this.shortCode = shortCode;
        }
        
        public String getOriginalUrl() {
            return originalUrl;
        }
        
        public void setOriginalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
        }
        
        public long getTotalClicks() {
            return totalClicks;
        }
        
        public void setTotalClicks(long totalClicks) {
            this.totalClicks = totalClicks;
        }
        
        public long getUniqueUsers() {
            return uniqueUsers;
        }
        
        public void setUniqueUsers(long uniqueUsers) {
            this.uniqueUsers = uniqueUsers;
        }
        
        public long getAnonymousClicks() {
            return anonymousClicks;
        }
        
        public void setAnonymousClicks(long anonymousClicks) {
            this.anonymousClicks = anonymousClicks;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
