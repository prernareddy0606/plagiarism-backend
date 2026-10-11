package com.plagiarism.repository;

import com.plagiarism.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByAnalysisDocumentUserIdOrderByCreatedAtDesc(Long userId);
}