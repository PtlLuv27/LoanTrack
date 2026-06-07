package com.loantrack.model;

import com.loantrack.enums.ApplicationStatus;
import com.loantrack.enums.EmploymentType;
import com.loantrack.enums.LoanPurpose;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "loan_applications")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Links back to the User who applied

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal loanAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanPurpose loanPurpose;

    @Column(nullable = false)
    private Integer tenureMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentType employmentType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyIncome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(updatable = false)
    private LocalDateTime appliedAt = LocalDateTime.now();

    // References to the staff who handled this
    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(columnDefinition = "TEXT")
    private String remarks;
}