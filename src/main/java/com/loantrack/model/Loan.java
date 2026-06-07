package com.loantrack.model;

import com.loantrack.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private LoanApplication application;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal principalAmount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private Integer tenureMonths;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal emiAmount;

    @Column(nullable = false)
    private LocalDate disbursementDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal outstandingBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus loanStatus = LoanStatus.ACTIVE;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalPaid = BigDecimal.ZERO;
}