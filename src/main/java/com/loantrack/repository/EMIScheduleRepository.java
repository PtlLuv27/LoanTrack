package com.loantrack.repository;

import com.loantrack.model.EMISchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EMIScheduleRepository extends JpaRepository<EMISchedule, Long> {
    List<EMISchedule> findByLoan_LoanIdOrderByEmiNumberAsc(Long loanId);
    // For the midnight penalty checker
    List<EMISchedule> findByPaymentStatusAndDueDateBefore(com.loantrack.enums.PaymentStatus status, java.time.LocalDate date);
    
    // For the 9 AM email reminder
    List<EMISchedule> findByPaymentStatusAndDueDate(com.loantrack.enums.PaymentStatus status, java.time.LocalDate date);
}