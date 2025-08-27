package com.example.sample_spring_boot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing database schema...");
        
        // Create users table if not exists
        String createUsersTableSql = """
            CREATE TABLE IF NOT EXISTS users (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                email VARCHAR(255) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                full_name VARCHAR(255) NOT NULL,
                created_at BIGINT NOT NULL
            )
            """;
        
        // Create urls table if not exists
        String createUrlsTableSql = """
            CREATE TABLE IF NOT EXISTS urls (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                original_url VARCHAR(2048) NOT NULL,
                short_code VARCHAR(10) NOT NULL UNIQUE,
                user_id INT NOT NULL,
                created_at BIGINT NOT NULL
            )
            """;
        
        try {
            // Create users table
            jdbcTemplate.execute(createUsersTableSql);
            System.out.println("✓ Table 'users' created or already exists");
            
            // Create urls table
            jdbcTemplate.execute(createUrlsTableSql);
            System.out.println("✓ Table 'urls' created or already exists");
            
            // Ensure user_id column is INT NOT NULL (in case it was created differently before)
            try {
                String alterUserIdSql = "ALTER TABLE urls MODIFY COLUMN user_id INT NOT NULL";
                jdbcTemplate.execute(alterUserIdSql);
                System.out.println("✓ Column 'user_id' set to INT NOT NULL");
            } catch (Exception e) {
                System.out.println("⚠ Could not alter user_id column (might already be correct): " + e.getMessage());
            }
            
            // Create indexes - check if they exist first
            createIndexIfNotExists("idx_short_code", "urls", "short_code");
            createIndexIfNotExists("idx_created_at", "urls", "created_at");
            
            System.out.println("Database initialization completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Error during database initialization: " + e.getMessage());
            throw e;
        }
    }
    
    private void createIndexIfNotExists(String indexName, String tableName, String columnName) {
        try {
            // Check if index exists
            String checkIndexSql = """
                SELECT COUNT(*) FROM information_schema.statistics 
                WHERE table_schema = DATABASE() 
                AND table_name = ? 
                AND index_name = ?
                """;
            
            Integer count = jdbcTemplate.queryForObject(checkIndexSql, Integer.class, tableName, indexName);
            
            if (count == null || count == 0) {
                // Index doesn't exist, create it
                String createIndexSql = "CREATE INDEX " + indexName + " ON " + tableName + "(" + columnName + ")";
                jdbcTemplate.execute(createIndexSql);
                System.out.println("✓ Index '" + indexName + "' created");
            } else {
                System.out.println("✓ Index '" + indexName + "' already exists");
            }
        } catch (Exception e) {
            System.out.println("⚠ Could not create index '" + indexName + "': " + e.getMessage());
            // Don't throw exception for index creation failures as they're not critical
        }
    }
}
