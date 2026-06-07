package com.loantrack.repository;

import com.loantrack.model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    // We will need this later to show a user their specific applications
    List<LoanApplication> findByUser_UserId(Long userId);
}