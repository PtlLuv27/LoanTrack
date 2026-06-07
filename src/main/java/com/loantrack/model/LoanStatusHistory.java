package com.loantrack.model;

import com.loantrack.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "loan_status_history")
public class LoanStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private LoanApplication application;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus oldStatus; // Nullable for the very first status

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus newStatus;

    @ManyToOne
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(updatable = false)
    private LocalDateTime changedAt = LocalDateTime.now();
}