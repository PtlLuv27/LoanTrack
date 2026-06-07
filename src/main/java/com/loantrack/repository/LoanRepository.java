package com.loantrack.repository;

import com.loantrack.enums.LoanStatus;
import com.loantrack.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List; 

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUser_UserId(Long userId);
    List<Loan> findByLoanStatus(com.loantrack.enums.LoanStatus status);
    // Get total money the bank has lent out
    @Query("SELECT SUM(l.principalAmount) FROM Loan l WHERE l.loanStatus = 'ACTIVE'")
    BigDecimal sumTotalDisbursed();

    // Get total money currently outstanding
    @Query("SELECT SUM(l.outstandingBalance) FROM Loan l WHERE l.loanStatus = 'ACTIVE'")
    BigDecimal sumTotalOutstanding();

    long countByLoanStatus(LoanStatus status);
}