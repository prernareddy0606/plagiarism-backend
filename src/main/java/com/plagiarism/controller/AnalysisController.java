package com.plagiarism.controller;

import com.plagiarism.entity.Document;
import com.plagiarism.repository.DocumentRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(originPatterns = "http://localhost:*")
public class AnalysisController {

    private static final int SHINGLE_SIZE = 4; // compare 4-word phrases

    private final DocumentRepository documentRepository;

    public AnalysisController(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @PostMapping("/{documentId}")
    public ResponseEntity<?> analyze(@PathVariable Long documentId) {

        Document target = documentRepository.findById(documentId).orElse(null);
        if (target == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Document not found"));
        }

        Set<String> targetShingles = shingles(target.getContent());
        if (targetShingles.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message",
                    "No readable text was found in this document (scanned PDFs have no text)."));
        }

        Set<String> matchedAnywhere = new HashSet<>();
        List<Map<String, Object>> matches = new ArrayList<>();

        for (Document other : documentRepository.findAll()) {
            if (other.getId().equals(documentId)) continue;

            Set<String> otherShingles = shingles(other.getContent());
            if (otherShingles.isEmpty()) continue;

            Set<String> common = new HashSet<>(targetShingles);
            common.retainAll(otherShingles);
            if (common.isEmpty()) continue;

            matchedAnywhere.addAll(common);

            Map<String, Object> match = new HashMap<>();
            match.put("documentId", other.getId());
            match.put("filename", other.getFilename());
            match.put("similarity", round(100.0 * common.size() / targetShingles.size()));
            matches.add(match);
        }

        matches.sort((a, b) ->
                Double.compare((double) b.get("similarity"), (double) a.get("similarity")));

        double overall = round(100.0 * matchedAnywhere.size() / targetShingles.size());

        Map<String, Object> result = new HashMap<>();
        result.put("documentId", documentId);
        result.put("filename", target.getFilename());
        result.put("plagiarismPercentage", overall);
        result.put("originalPercentage", round(100.0 - overall));
        result.put("documentsCompared", documentRepository.count() - 1);
        result.put("matches", matches);

        return ResponseEntity.ok(result);
    }

    // Splits text into overlapping groups of words
    private Set<String> shingles(String text) {
        Set<String> result = new HashSet<>();
        if (text == null) return result;

        String[] words = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").trim().split("\\s+");
        if (words.length == 1 && words[0].isEmpty()) return result;

        int n = Math.min(SHINGLE_SIZE, words.length);
        for (int i = 0; i + n <= words.length; i++) {
            result.add(String.join(" ", Arrays.copyOfRange(words, i, i + n)));
        }
        return result;
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}