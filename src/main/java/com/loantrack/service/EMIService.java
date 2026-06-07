package com.loantrack.service;

import com.loantrack.enums.PaymentStatus;
import com.loantrack.model.EMISchedule;
import com.loantrack.model.Loan;
import com.loantrack.repository.EMIScheduleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class EMIService {

    private final EMIScheduleRepository emiRepository;

    public EMIService(EMIScheduleRepository emiRepository) {
        this.emiRepository = emiRepository;
    }

    // Core EMI Formula: P * r * (1 + r)^n / [(1 + r)^n - 1]
    public BigDecimal calculateEMI(BigDecimal principal, double annualRate, int tenureMonths) {
        double r = annualRate / 12 / 100; // Monthly interest rate
        double p = principal.doubleValue();
        double numerator = p * r * Math.pow(1 + r, tenureMonths);
        double denominator = Math.pow(1 + r, tenureMonths) - 1;
        
        return BigDecimal.valueOf(numerator / denominator).setScale(2, RoundingMode.HALF_UP);
    }

    public void generateSchedule(Loan loan) {
        BigDecimal balance = loan.getPrincipalAmount();
        double r = loan.getInterestRate().doubleValue() / 12 / 100;
        LocalDate dueDate = loan.getDisbursementDate().plusMonths(1);

        for (int i = 1; i <= loan.getTenureMonths(); i++) {
            // Calculate interest for this specific month based on remaining balance
            BigDecimal interest = balance.multiply(BigDecimal.valueOf(r)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principal = loan.getEmiAmount().subtract(interest);
            balance = balance.subtract(principal);

            // Correct tiny rounding errors on the final month to ensure balance hits exactly 0.00
            if (i == loan.getTenureMonths() && balance.compareTo(BigDecimal.ZERO) != 0) {
                principal = principal.add(balance);
                balance = BigDecimal.ZERO;
            }

            EMISchedule emi = new EMISchedule();
            emi.setLoan(loan);
            emi.setEmiNumber(i);
            emi.setDueDate(dueDate);
            emi.setEmiAmount(loan.getEmiAmount());
            emi.setPrincipalComponent(principal);
            emi.setInterestComponent(interest);
            emi.setPaymentStatus(PaymentStatus.UPCOMING);
            
            emiRepository.save(emi);
            dueDate = dueDate.plusMonths(1);
        }
    }
}