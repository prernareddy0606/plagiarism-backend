package com.plagiarism.repository;

import com.plagiarism.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    Optional<Analysis> findByDocumentId(Long documentId);

    List<Analysis> findByDocumentUserIdOrderByCreatedAtDesc(Long userId);
}