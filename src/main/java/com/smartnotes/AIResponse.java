package com.bhanu.smartnotes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * This class represents the JSON response we get *back* from the Gemini API.
 * We only define the fields we care about (candidates -> content -> parts -> text).
 * @JsonIgnoreProperties(ignoreUnknown = true) is very important.
 * It tells Jackson to safely ignore all the extra fields in the AI's
 * response that we don't need (like "promptFeedback").
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AIResponse {

    private List<Candidate> candidates;

    public List<Candidate> getCandidates() { return candidates; }
    public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        private Content content;
        public Content getContent() { return content; }
        public void setContent(Content content) { this.content = content; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private List<Part> parts;
        public List<Part> getParts() { return parts; }
        public void setParts(List<Part> parts) { this.parts = parts; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        private String text;
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}

