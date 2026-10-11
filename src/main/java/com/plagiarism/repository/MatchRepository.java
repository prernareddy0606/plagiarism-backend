package com.plagiarism.repository;

import com.plagiarism.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByAnalysisId(Long analysisId);
}