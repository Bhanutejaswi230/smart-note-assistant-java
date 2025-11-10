-- This SQL file will be run automatically by Spring Boot
-- It creates the 'note' table that matches your 'Note' class
CREATE TABLE IF NOT EXISTS note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    topic VARCHAR(255) NOT NULL,
    content TEXT,
    keywords VARCHAR(255),
    timestamp VARCHAR(100)
);