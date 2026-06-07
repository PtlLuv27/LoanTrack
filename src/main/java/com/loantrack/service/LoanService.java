package com.loantrack.service;

import com.loantrack.enums.LoanStatus;
import com.loantrack.model.Loan;
import com.loantrack.model.LoanApplication;
import com.loantrack.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final EMIService emiService;

    public LoanService(LoanRepository loanRepository, EMIService emiService) {
        this.loanRepository = loanRepository;
        this.emiService = emiService;
    }

    public void createLoanFromApplication(LoanApplication application) {
        Loan loan = new Loan();
        loan.setApplication(application);
        loan.setUser(application.getUser());
        loan.setPrincipalAmount(application.getLoanAmount());
        
        // For this project, we are setting a flat 12% annual interest rate for all loans
        BigDecimal interestRate = new BigDecimal("12.00");
        loan.setInterestRate(interestRate);
        loan.setTenureMonths(application.getTenureMonths());
        
        // Let the EMIService calculate the exact monthly payment
        BigDecimal emiAmount = emiService.calculateEMI(loan.getPrincipalAmount(), interestRate.doubleValue(), loan.getTenureMonths());
        loan.setEmiAmount(emiAmount);
        
        loan.setDisbursementDate(LocalDate.now());
        loan.setEndDate(LocalDate.now().plusMonths(loan.getTenureMonths()));
        loan.setOutstandingBalance(loan.getPrincipalAmount());
        loan.setLoanStatus(LoanStatus.ACTIVE);
        
        Loan savedLoan = loanRepository.save(loan);
        
        // Instantly generate the 1-to-N month schedule!
        emiService.generateSchedule(savedLoan);
    }
}