package com.loantrack.repository;

import com.loantrack.model.Payment;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByLoan_LoanIdOrderByPaymentDateDesc(Long loanId);
    // Get total money the bank has collected from all users
    @Query("SELECT SUM(p.amountPaid) FROM Payment p WHERE p.status = 'SUCCESS'")
    BigDecimal sumTotalCollected();
}