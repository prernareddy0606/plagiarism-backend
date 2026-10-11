package com.plagiarism.repository;

import com.plagiarism.entity.Document;
import com.plagiarism.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUser(User user);

    List<Document> findByUserId(Long userId);
}