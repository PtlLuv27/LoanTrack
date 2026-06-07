package com.loantrack.repository;

import com.loantrack.model.LoanStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanStatusHistoryRepository extends JpaRepository<LoanStatusHistory, Long> {
    List<LoanStatusHistory> findByApplication_ApplicationIdOrderByChangedAtDesc(Long applicationId);
}