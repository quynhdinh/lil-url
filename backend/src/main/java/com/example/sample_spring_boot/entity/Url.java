package com.example.sample_spring_boot.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "urls")
public class Url {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;
    
    @Column(name = "short_code", nullable = false, unique = true, length = 10)
    private String shortCode;
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    @Column(name = "created_at", nullable = false)
    private Long createdAt;
    
    // Default constructor
    public Url() {}
    
    // Constructor with userId (required since userId is non-nullable)
    public Url(String originalUrl, String shortCode, Integer userId) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.userId = userId;
        this.createdAt = System.currentTimeMillis();
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public Long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = System.currentTimeMillis();
        }
    }
}
