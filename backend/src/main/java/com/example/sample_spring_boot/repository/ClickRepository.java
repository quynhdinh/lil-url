package com.example.sample_spring_boot.repository;

import com.example.sample_spring_boot.entity.Click;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClickRepository extends JpaRepository<Click, Long> {
    
    /**
     * Find all clicks for a specific short code
     */
    List<Click> findByShortCode(String shortCode);
    
    /**
     * Find all clicks for a specific user
     */
    List<Click> findByUserId(Integer userId);
    
    /**
     * Count clicks for a specific short code
     */
    long countByShortCode(String shortCode);
    
    /**
     * Count clicks for a specific user
     */
    long countByUserId(Integer userId);
    
    /**
     * Find clicks by short code and user ID
     */
    List<Click> findByShortCodeAndUserId(String shortCode, Integer userId);
    
    /**
     * Get click statistics for a short code
     */
    @Query("SELECT COUNT(c) FROM Click c WHERE c.shortCode = :shortCode")
    long getClickCountByShortCode(@Param("shortCode") String shortCode);
}
