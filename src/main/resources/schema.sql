CREATE TABLE IF NOT EXISTS note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    topic VARCHAR(255) NOT NULL,
    content TEXT,
    keywords VARCHAR(255),
    timestamp VARCHAR(100)
);
