package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@EnableJdbcRepositories // Enable Spring Data JDBC
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * Creates a WebClient.Builder bean that the NoteController can use.
     * Spring Boot automatically manages this bean.
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    /**
     * Creates an ObjectMapper bean that the NoteController can use for JSON parsing.
     * Spring Boot automatically manages this bean.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}