package com.example.sample_spring_boot.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clicks")
public class Click {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = true)
    private Integer userId;
    
    @Column(name = "short_code", nullable = false, length = 10)
    private String shortCode;
    
    @Column(name = "created_at", nullable = false)
    private Long createdAt;
    
    // Default constructor
    public Click() {}
    
    // Constructor
    public Click(Integer userId, String shortCode) {
        this.userId = userId;
        this.shortCode = shortCode;
        this.createdAt = System.currentTimeMillis();
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    @Override
    public String toString() {
        return "Click{" +
                "id=" + id +
                ", userId=" + userId +
                ", shortCode='" + shortCode + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
