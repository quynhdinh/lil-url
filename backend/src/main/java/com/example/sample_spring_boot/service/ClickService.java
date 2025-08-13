package com.example.sample_spring_boot.service;

import com.example.sample_spring_boot.entity.Click;
import com.example.sample_spring_boot.entity.Url;
import com.example.sample_spring_boot.repository.ClickRepository;
import com.example.sample_spring_boot.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClickService {

    private final ClickRepository clickRepository;

    private final UrlRepository urlRepository;

    public ClickService(ClickRepository clickRepository, UrlRepository urlRepository) {
        this.clickRepository = clickRepository;
        this.urlRepository = urlRepository;
    }

    /**
     * Record a click for a short URL
     * @param shortCode The short code that was clicked
     * @param userId The user who clicked (null for anonymous)
     * @return The created Click entity
     */
    public Click recordClick(String shortCode, Integer userId) {
        Click click = new Click(userId, shortCode);
        System.out.println("Recording click: " + click);
        return clickRepository.save(click);
    }

    /**
     * Get total click count for a short code
     * @param shortCode The short code to count clicks for
     * @return Number of clicks
     */
    public long getClickCount(String shortCode) {
        return clickRepository.countByShortCode(shortCode);
    }

    /**
     * Get all clicks for a specific short code
     * @param shortCode The short code to get clicks for
     * @return List of clicks
     */
    public List<Click> getClicksByShortCode(String shortCode) {
        return clickRepository.findByShortCode(shortCode);
    }

    /**
     * Get all clicks by a specific user
     * @param userId The user ID to get clicks for
     * @return List of clicks by the user
     */
    public List<Click> getClicksByUserId(Integer userId) {
        return clickRepository.findByUserId(userId);
    }
    
    public long countByUserId(Integer userId) {
        return clickRepository.countByUserId(userId);
    }

    /**
     * Get click statistics for a URL
     * @param shortCode The short code to get stats for
     * @return ClickStats object with detailed information
     */
    public ClickStats getClickStats(String shortCode) {
        List<Click> clicks = clickRepository.findByShortCode(shortCode);
        long totalClicks = clicks.size();
        long uniqueUsers = clicks.stream()
                .filter(click -> click.getUserId() != null)
                .mapToInt(Click::getUserId)
                .distinct()
                .count();
        long anonymousClicks = clicks.stream()
                .filter(click -> click.getUserId() == null)
                .count();

        Optional<Url> url = urlRepository.findByShortCode(shortCode);
        String originalUrl = url.map(Url::getOriginalUrl).orElse("Unknown");

        return new ClickStats(shortCode, originalUrl, totalClicks, uniqueUsers, anonymousClicks, clicks);
    }

    /**
     * Get total clicks for a user across all their URLs
     * @param userId The user ID
     * @return Total click count
     */
    public long getTotalClicksForUser(Integer userId) {
        return clickRepository.countByUserId(userId);
    }

    /**
     * Check if a URL exists before recording a click
     * @param shortCode The short code to validate
     * @return true if the URL exists, false otherwise
     */
    public boolean isValidShortCode(String shortCode) {
        return urlRepository.existsByShortCode(shortCode);
    }

    /**
     * Record a click with validation
     * @param shortCode The short code that was clicked
     * @param userId The user who clicked (null for anonymous)
     * @return The created Click entity, or null if invalid short code
     */
    public Click recordValidatedClick(String shortCode, Integer userId) {
        if (!isValidShortCode(shortCode)) {
            return null;
        }
        return recordClick(shortCode, userId);
    }

    /**
     * Delete all clicks for a specific short code
     * @param shortCode The short code to delete clicks for
     */
    public void deleteClicksByShortCode(String shortCode) {
        List<Click> clicks = clickRepository.findByShortCode(shortCode);
        clickRepository.deleteAll(clicks);
    }

    public record ClickStats(String shortCode, String originalUrl, long totalClicks,
                              long uniqueUsers, long anonymousClicks, List<Click> recentClicks) {
    }
}
