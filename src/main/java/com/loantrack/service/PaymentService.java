package com.loantrack.service;

import com.loantrack.enums.PaymentMode;
import com.loantrack.enums.PaymentStatus;
import com.loantrack.enums.TransactionStatus;
import com.loantrack.model.EMISchedule;
import com.loantrack.model.Loan;
import com.loantrack.model.Payment;
import com.loantrack.repository.EMIScheduleRepository;
import com.loantrack.repository.LoanRepository;
import com.loantrack.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EMIScheduleRepository emiRepository;
    private final LoanRepository loanRepository;
    
    // 1. Added the CreditScoreService field
    private final CreditScoreService creditScoreService; 

    // 2. Added CreditScoreService to the constructor
    public PaymentService(PaymentRepository paymentRepository, 
                          EMIScheduleRepository emiRepository, 
                          LoanRepository loanRepository,
                          CreditScoreService creditScoreService) {
        this.paymentRepository = paymentRepository;
        this.emiRepository = emiRepository;
        this.loanRepository = loanRepository;
        this.creditScoreService = creditScoreService; // 3. Initialized the field
    }

    // @Transactional ensures that if any part of this fails, the whole database transaction rolls back automatically
    @Transactional
    public void processEMIPayment(Long emiId, PaymentMode paymentMode) {
        EMISchedule emi = emiRepository.findById(emiId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid EMI ID"));

        if (emi.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("This EMI is already paid!");
        }

        Loan loan = emi.getLoan();

        // 1. Create the Payment Record
        Payment payment = new Payment();
        payment.setLoan(loan);
        payment.setEmiSchedule(emi);
        payment.setUser(loan.getUser());
        
        // Total amount is the EMI + any accrued penalties
        BigDecimal totalToPay = emi.getEmiAmount().add(emi.getPenaltyAmount());
        payment.setAmountPaid(totalToPay);
        payment.setPaymentMode(paymentMode);
        payment.setPenaltyPaid(emi.getPenaltyAmount());
        payment.setTransactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setStatus(TransactionStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        
        paymentRepository.save(payment);

        // 2. Update the EMI Record
        emi.setPaymentStatus(PaymentStatus.PAID);
        emi.setPaidDate(LocalDate.now());
        emi.setPaidAmount(totalToPay);
        emiRepository.save(emi);

        // 3. Update the Loan Balances
        loan.setTotalPaid(loan.getTotalPaid().add(emi.getEmiAmount()));
        
        // Outstanding balance only reduces by the principal portion of the payment, not the interest!
        BigDecimal newBalance = loan.getOutstandingBalance().subtract(emi.getPrincipalComponent());
        // Prevent negative balances due to minor rounding fractions
        if(newBalance.compareTo(BigDecimal.ZERO) < 0) {
            newBalance = BigDecimal.ZERO; 
        }
        loan.setOutstandingBalance(newBalance);
        loanRepository.save(loan);
        
        // 4. Update Credit Score
        creditScoreService.updateScoreOnPayment(loan.getUser(), emi);
    }
}