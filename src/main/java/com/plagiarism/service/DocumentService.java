package com.plagiarism.service;

import com.plagiarism.entity.Document;
import com.plagiarism.entity.User;
import com.plagiarism.repository.DocumentRepository;
import com.plagiarism.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final TextExtractionService textExtractionService;

    public DocumentService(
            DocumentRepository documentRepository,
            UserRepository userRepository,
            TextExtractionService textExtractionService) {

        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.textExtractionService = textExtractionService;
    }

    public Document uploadDocument(
            MultipartFile file,
            Long userId) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String extractedText =
                textExtractionService.extractText(file);

        Document document = new Document();

        document.setUser(user);
        document.setFilename(file.getOriginalFilename());
        document.setFileType(file.getContentType());
        document.setContent(extractedText);

        return documentRepository.save(document);
    }

    public List<Document> getUserDocuments(Long userId) {

        return documentRepository.findByUserId(userId);
    }

    public Document getDocument(Long id) {

        return documentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Document not found"));
    }

    public void deleteDocument(Long id) {

        if (!documentRepository.existsById(id)) {
            throw new RuntimeException("Document not found");
        }

        documentRepository.deleteById(id);
    }
}