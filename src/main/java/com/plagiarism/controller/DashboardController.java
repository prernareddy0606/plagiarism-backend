package com.plagiarism.controller;

import com.plagiarism.repository.AnalysisRepository;
import com.plagiarism.repository.DocumentRepository;
import com.plagiarism.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(originPatterns = "http://localhost:*")
public class DashboardController {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final AnalysisRepository analysisRepository;

    public DashboardController(
            UserRepository userRepository,
            DocumentRepository documentRepository,
            AnalysisRepository analysisRepository) {

        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.analysisRepository = analysisRepository;
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<String, Object>> getStats(
            @PathVariable Long userId) {

        Map<String, Object> stats = new HashMap<>();

        stats.put(
                "totalDocuments",
                documentRepository.findByUserId(userId).size()
        );

        stats.put(
                "totalAnalyses",
                analysisRepository
                        .findByDocumentUserIdOrderByCreatedAtDesc(userId)
                        .size()
        );

        stats.put(
                "totalUsers",
                userRepository.count()
        );

        return ResponseEntity.ok(stats);
    }
}