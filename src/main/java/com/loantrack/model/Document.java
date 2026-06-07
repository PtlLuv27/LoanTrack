package com.loantrack.model;

import com.loantrack.enums.DocumentType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long docId;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private LoanApplication application;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType docType;

    @Column(nullable = false, length = 500)
    private String filePath;

    @Column(nullable = false, length = 200)
    private String originalFilename;

    @Column(nullable = false)
    private Integer fileSizeKb;

    @Column(updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();
}