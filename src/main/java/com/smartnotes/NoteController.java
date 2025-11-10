package com.bhanu.smartnotes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api") 
public class NoteController {

    private static final Logger log = LoggerFactory.getLogger(NoteController.class);
    private final NoteRepository noteRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String geminiApiKey = "AIzaSyDNQpVxoJ4_y741QZfI9k2sq8TE33hfLmI"; // Your API Key
    private final String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent?key=" + geminiApiKey;
    private final AIPayload.GenerationConfig.Schema geminiNoteSchema = new AIPayload.GenerationConfig.Schema(
            "OBJECT",
            Map.of(
                    "topic", Map.of("type", "STRING", "description", "A concise title (5 words max)"),
                    "content", Map.of("type", "STRING", "description", "Detailed key points using markdown headings (##) and nested bullet points (- item,   * sub-item)"),
                    "keywords", Map.of("type", "STRING", "description", "A comma-separated string of 5-7 relevant keywords")
            ),
            List.of("topic", "content", "keywords") 
    );
    private final String systemPromptForNoteProcessing =
            "You are a helpful note-taking assistant. " +
                    "Analyze the user's raw text and structure it precisely according to the schema. " +
                    "1. 'topic': Create a concise, descriptive title (max 5 words). " +
                    "2. 'content': Extract the key information and structure it using markdown. Use headings (## Heading) for main sections and nested bullet points (- Main point,   * Sub-point) for details. Be detailed and accurate, do not over-summarize. " +
                    "3. 'keywords': Extract the 5-7 most relevant keywords and return them as a single comma-separated string. " +
                    "Your response MUST be ONLY the valid JSON object conforming to the schema.";

    public NoteController(NoteRepository noteRepository, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.noteRepository = noteRepository;
        this.webClient = webClientBuilder.baseUrl("https://generativelanguage.googleapis.com").build();
        this.objectMapper = objectMapper;
    }
    @PostMapping("/ai-process")
    public Mono<Note> processNoteWithAI(@RequestBody String rawText) {
        log.info("Received request to AI-process note");
        AIPayload aiPayload = new AIPayload(rawText, systemPromptForNoteProcessing, geminiNoteSchema);

        return webClient.post()
                .uri("/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent?key=" + geminiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(aiPayload)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("[No error body provided]")
                                .flatMap(errorBody -> {
                                    log.error("Error from Gemini API: Status={}, Body={}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("Error from Gemini API: " + clientResponse.statusCode() + " - " + errorBody));
                                })
                )
                .bodyToMono(AIResponse.class)
                .flatMap(aiResponse -> {
                    try {
                        if (aiResponse == null || aiResponse.getCandidates() == null || aiResponse.getCandidates().isEmpty() ||
                                aiResponse.getCandidates().get(0).getContent() == null || aiResponse.getCandidates().get(0).getContent().getParts() == null ||
                                aiResponse.getCandidates().get(0).getContent().getParts().isEmpty()) {
                            log.error("AI response was empty or malformed: {}", aiResponse);
                            return Mono.error(new RuntimeException("AI response was empty or malformed."));
                        }
                        String jsonText = aiResponse.getCandidates().get(0).getContent().getParts().get(0).getText();
                        log.debug("Received JSON text from AI: {}", jsonText);

                        Note structuredNote = objectMapper.readValue(jsonText, Note.class);

                        // Basic validation or default values if AI fails schema
                        if (structuredNote.getTopic() == null) structuredNote.setTopic("Untitled Note");
                        if (structuredNote.getContent() == null) structuredNote.setContent("");
                        if (structuredNote.getKeywords() == null) structuredNote.setKeywords("");

                        structuredNote.setTimestamp(Instant.now().toString());
                        Note savedNote = noteRepository.save(structuredNote);
                        log.info("Successfully processed and saved note ID: {}", savedNote.getId());
                        return Mono.just(savedNote);

                    } catch (Exception e) {
                        log.error("Error processing AI response content", e);
                        return Mono.error(new RuntimeException("Error processing AI response content: " + e.getMessage()));
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error in AI processing reactive chain", e);
                    return Mono.error(e instanceof RuntimeException ? e : new RuntimeException("Unexpected error during AI processing", e));
                });
    }

    @GetMapping("/notes")
    public Iterable<Note> getAllNotes() {
        log.info("Received /notes request");
        return noteRepository.findAll();
    }

    @DeleteMapping("/notes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(@PathVariable Long id) {
        log.info("Received request to delete note ID: {}", id);
        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
            log.info("Successfully deleted note ID: {}", id);
        } else {
            log.warn("Attempted to delete non-existent note ID: {}", id);
        }
    }
}

