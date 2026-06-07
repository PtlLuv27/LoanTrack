package com.loantrack.dto;

import com.loantrack.enums.EmploymentType;
import com.loantrack.enums.LoanPurpose;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanApplicationDTO {

    @NotNull(message = "Loan amount is required")
    @Min(value = 10000, message = "Minimum loan amount is ₹10,000")
    @Max(value = 5000000, message = "Maximum loan amount is ₹50,00,000")
    private BigDecimal loanAmount;

    @NotNull(message = "Loan purpose is required")
    private LoanPurpose loanPurpose;

    @NotNull(message = "Tenure is required")
    private Integer tenureMonths;

    @NotNull(message = "Employment type is required")
    private EmploymentType employmentType;

    @NotNull(message = "Monthly income is required")
    @Min(value = 15000, message = "Minimum monthly income must be ₹15,000")
    private BigDecimal monthlyIncome;
}