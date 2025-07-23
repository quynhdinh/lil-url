-- Create table users if not exists
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    created_at BIGINT NOT NULL
);

-- Create table urls if not exists
CREATE TABLE IF NOT EXISTS urls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_url VARCHAR(2048) NOT NULL,
    short_code VARCHAR(10) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    created_at BIGINT NOT NULL
);

-- Alter table to make user_id INT NOT NULL (in case it was created differently before)
ALTER TABLE urls MODIFY COLUMN user_id INT NOT NULL;
