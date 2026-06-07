package com.loantrack.model;

import com.loantrack.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "emi_schedule")
public class EMISchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emiId;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(nullable = false)
    private Integer emiNumber;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal emiAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal principalComponent;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal interestComponent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.UPCOMING;

    private LocalDate paidDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal paidAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal penaltyAmount = BigDecimal.ZERO;
}