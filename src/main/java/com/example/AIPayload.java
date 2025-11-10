package com.example;

import java.util.List;
import java.util.Map;

/**
 * This class represents the JSON payload we send *to* the Gemini API.
 * It is structured to match the API's requirements.
 * It contains nested classes (Content, Part, GenerationConfig, Schema) 
 * because the API requires a complex, nested JSON object.
 */
public class AIPayload {

    private List<Content> contents;
    private GenerationConfig generationConfig;

    /**
     * Constructor to build the payload for our specific note-processing request.
     */
    public AIPayload(String rawText, String prompt, GenerationConfig.Schema schema) {
        // We send two parts: the system prompt (instructions) and the user's text.
        // In the new API, system prompt is a separate field, but for this model
        // we can send it as the first part of the 'contents' array.
        this.contents = List.of(
            // The AI will treat the first "user" role as the system prompt
            new Content(List.of(new Part(prompt))), 
            new Content(List.of(new Part(rawText)))
        );
        
        // Set the generation config to force JSON output
        this.generationConfig = new GenerationConfig("application/json", schema);
    }

    // Getters are needed for Jackson (the JSON library) to serialize this object
    public List<Content> getContents() { return contents; }
    public GenerationConfig getGenerationConfig() { return generationConfig; }

    // --- All Nested Helper Classes ---
    // These classes match the complex JSON structure required by the Gemini API.

    static class Content {
        private List<Part> parts;
        // The API also needs a "role", but it defaults to "user", which is fine for both parts.
        Content(List<Part> parts) { this.parts = parts; }
        public List<Part> getParts() { return parts; }
    }

    static class Part {
        private String text;
        Part(String text) { this.text = text; }
        public String getText() { return text; }
    }

    /**
     * This class is nested inside AIPayload because it's only used here.
     * It tells the AI *how* to generate the response (e.g., as JSON).
     */
    static class GenerationConfig {
        private String responseMimeType;
        private Schema responseSchema;

        GenerationConfig(String mimeType, Schema schema) {
            this.responseMimeType = mimeType;
            this.responseSchema = schema;
        }
        
        public String getResponseMimeType() { return responseMimeType; }
        public Schema getResponseSchema() { return responseSchema; }

        /**
         * This class is *also* nested. It defines the exact JSON structure
         * we want the AI to return. This is what NoteController was missing.
         */
        static class Schema {
            private String type;
            private Map<String, Object> properties;
            private List<String> propertyOrdering;

            Schema(String type, Map<String, Object> properties, List<String> propertyOrdering) {
                this.type = type;
                this.properties = properties;
                this.propertyOrdering = propertyOrdering;
            }

            public String getType() { return type; }
            public Map<String, Object> getProperties() { return properties; }
            public List<String> getPropertyOrdering() { return propertyOrdering; }
        }
    }
}

