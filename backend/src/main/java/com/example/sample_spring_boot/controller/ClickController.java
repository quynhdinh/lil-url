package com.example.sample_spring_boot.controller;

import com.example.sample_spring_boot.entity.Url;
import com.example.sample_spring_boot.entity.User;
import com.example.sample_spring_boot.service.ClickService;
import com.example.sample_spring_boot.service.UrlService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ClickController {
    
    private final ClickService clickService;

    private final UrlService urlService;

    public ClickController(ClickService clickService, UrlService urlService) {
        this.clickService = clickService;
        this.urlService = urlService;
    }

    /**
     * Get link count (number of URLs created) by user
     * @param userId The user ID
     * @return Response with link count
     */
    @GetMapping("/clicks/count")
    public ResponseEntity<LinkCountResponse> getLinkCountByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Integer userId = user.getId();

        long linkCount = clickService.countByUserId(userId);

        return ResponseEntity.ok(new LinkCountResponse(
            userId,
            linkCount,
            "Link count retrieved successfully"
        ));
    }
    
    /**
     * Get total clicks by user (across all their URLs)
     * @param userId The user ID
     * @return Response with total click count
     */
    @GetMapping("/users/{userId}/total-clicks")
    public ResponseEntity<TotalClicksResponse> getTotalClicksByUser(@PathVariable Integer userId) {
        // Get all URLs created by the user
        List<Url> userUrls = urlService.findByUserId(userId);
        
        // Count total clicks across all user's URLs
        long totalClicks = userUrls.stream()
                .mapToLong(url -> clickService.getClickCount(url.getShortCode()))
                .sum();
        
        return ResponseEntity.ok(new TotalClicksResponse(
            userId,
            totalClicks,
            userUrls.size(),
            "Total clicks retrieved successfully"
        ));
    }
    
    /**
     * Get comprehensive user statistics (both link count and total clicks)
     * @param userId The user ID
     * @return Response with comprehensive stats
     */
    @GetMapping("/users/{userId}/stats")
    public ResponseEntity<UserStatsResponse> getUserStats(@PathVariable Integer userId) {
        // Get link count
        long linkCount = urlService.countByUserId(userId);

        // Get all URLs created by the user
        List<Url> userUrls = urlService.findByUserId(userId);

        // Count total clicks across all user's URLs
        long totalClicks = userUrls.stream()
                .mapToLong(url -> clickService.getClickCount(url.getShortCode()))
                .sum();
        
        // Calculate average clicks per URL
        double averageClicksPerUrl = linkCount > 0 ? (double) totalClicks / linkCount : 0.0;
        
        return ResponseEntity.ok(new UserStatsResponse(
            userId,
            linkCount,
            totalClicks,
            averageClicksPerUrl,
            "User statistics retrieved successfully"
        ));
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
        Optional<Url> url = urlService.findByShortCodeAndUserId(shortCode, userId);

        if (url.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Get click statistics for this URL
        ClickService.ClickStats clickStats = clickService.getClickStats(shortCode);
        
        return ResponseEntity.ok(new UrlClicksResponse(
            userId,
            shortCode,
            clickStats.getOriginalUrl(),
            clickStats.getTotalClicks(),
            clickStats.getUniqueUsers(),
            clickStats.getAnonymousClicks(),
            "URL clicks retrieved successfully"
        ));
    }
    
    public record LinkCountResponse(
        Integer userId,
        long linkCount,
        String message
    ) {}
    
    public record TotalClicksResponse(
        Integer userId,
        long totalClicks,
        long urlCount,
        String message
    ) {}
    
    public record UserStatsResponse(
        Integer userId,
        long linkCount,
        long totalClicks,
        double averageClicksPerUrl,
        String message
    ) {}
    
    public record UrlClicksResponse(
        Integer userId,
        String shortCode,
        String originalUrl,
        long totalClicks,
        long uniqueUsers,
        long anonymousClicks,
        String message
    ) {}
}
