package com.loantrack.repository;

import com.loantrack.model.CreditScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditScoreRepository extends JpaRepository<CreditScore, Long> {
    // Fetches the entire history for the table
    List<CreditScore> findByUser_UserIdOrderByRecordedAtDesc(Long userId);
    
    // Grabs the single most recent score to display on the dashboard
    Optional<CreditScore> findTopByUser_UserIdOrderByRecordedAtDesc(Long userId);
}