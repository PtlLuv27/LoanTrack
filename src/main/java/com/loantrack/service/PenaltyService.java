package com.loantrack.service;

import com.loantrack.model.EMISchedule;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PenaltyService {

    // Calculates a 2% monthly penalty pro-rated by the exact number of days overdue
    public BigDecimal calculate(EMISchedule emi, long overdueDays) {
        BigDecimal penaltyRate = new BigDecimal("0.02"); // 2% 
        
        // (overdueDays / 30)
        BigDecimal timeRatio = BigDecimal.valueOf(overdueDays)
                .divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_UP);
        
        // EMI Amount * 2% * (days / 30)
        return emi.getEmiAmount()
                .multiply(penaltyRate)
                .multiply(timeRatio)
                .setScale(2, RoundingMode.HALF_UP);
    }
}