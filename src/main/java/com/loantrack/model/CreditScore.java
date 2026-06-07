package com.loantrack.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "credit_scores")
public class CreditScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scoreId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer scoreValue;

    @Column(nullable = false)
    private Integer changeAmount;

    @Column(nullable = false, length = 200)
    private String changeReason;

    @ManyToOne
    @JoinColumn(name = "related_emi_id")
    private EMISchedule relatedEmi; // Nullable, as not all score changes are EMI related

    @Column(updatable = false)
    private LocalDateTime recordedAt = LocalDateTime.now();
}