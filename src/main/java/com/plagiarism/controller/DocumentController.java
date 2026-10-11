package com.plagiarism.controller;

import com.plagiarism.entity.Document;
import com.plagiarism.service.DocumentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(originPatterns = "http://localhost:*")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {

        try {

            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Please select a file");
            }

            Document document =
                    documentService.uploadDocument(file, userId);

            return ResponseEntity.ok(document);

        } catch (IOException e) {

            return ResponseEntity.internalServerError()
                    .body("Error uploading document");

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Document>> getUserDocuments(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                documentService.getUserDocuments(userId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocument(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                documentService.getDocument(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(
            @PathVariable Long id) {

        documentService.deleteDocument(id);

        return ResponseEntity.ok(
                "Document deleted successfully"
        );
    }
}