package com.bhanu.smartnotes;

import org.springframework.data.annotation.Id;

/**
 * This is the Data Model (POJO) for our application.
 * Its fields directly map to the columns in the 'note' table
 * defined in 'schema.sql'.
 *
 * This version is the CORRECT one and matches your schema:
 * - id
 * - topic
 * - content
 * - keywords
 * - timestamp
 */
public class Note {

    @Id // Marks this field as the Primary Key (matches 'id BIGINT AUTO_INCREMENT PRIMARY KEY')
    private Long id;

    private String topic;     // Matches 'topic VARCHAR(255) NOT NULL'
    private String content;   // Matches 'content TEXT'
    private String keywords;  // Matches 'keywords VARCHAR(255)'
    private String timestamp; // Matches 'timestamp VARCHAR(100)'

    /**
     * A no-argument constructor is required by Spring Data JDBC
     * to create new instances of this object from database results.
     */
    public Note() {
    }

    // --- Getters and Setters for all fields ---
    // These are required for Spring and Jackson (JSON) to read/write data.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}