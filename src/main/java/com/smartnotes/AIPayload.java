package com.bhanu.smartnotes;

import java.util.List;
import java.util.Map;

public class AIPayload {

    private List<Content> contents;
    private GenerationConfig generationConfig;

    public AIPayload(String rawText, String prompt, GenerationConfig.Schema schema) {

        this.contents = List.of(
            new Content(List.of(new Part(prompt))), 
            new Content(List.of(new Part(rawText)))
        );
        this.generationConfig = new GenerationConfig("application/json", schema);
    }
    public List<Content> getContents() { return contents; }
    public GenerationConfig getGenerationConfig() { return generationConfig; }
    static class Content {
        private List<Part> parts;
        Content(List<Part> parts) { this.parts = parts; }
        public List<Part> getParts() { return parts; }
    }

    static class Part {
        private String text;
        Part(String text) { this.text = text; }
        public String getText() { return text; }
    }

    static class GenerationConfig {
        private String responseMimeType;
        private Schema responseSchema;

        GenerationConfig(String mimeType, Schema schema) {
            this.responseMimeType = mimeType;
            this.responseSchema = schema;
        }
        
        public String getResponseMimeType() { return responseMimeType; }
        public Schema getResponseSchema() { return responseSchema; }

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

