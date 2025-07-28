package com.example.sample_spring_boot.repository;

import com.example.sample_spring_boot.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    
    /**
     * Find URL by short code
     */
    Optional<Url> findByShortCode(String shortCode);
    
    /**
     * Check if short code exists
     */
    boolean existsByShortCode(String shortCode);
    
    /**
     * Find URL by original URL
     */
    Optional<Url> findByOriginalUrl(String originalUrl);
    
    /**
     * Find all URLs created by a specific user
     */
    List<Url> findByUserId(Integer userId);
    
    /**
     * Count URLs created by a specific user
     */
    long countByUserId(Integer userId);
    
    /**
     * Find a specific URL by short code and user ID
     */
    Optional<Url> findByShortCodeAndUserId(String shortCode, Integer userId);
}
