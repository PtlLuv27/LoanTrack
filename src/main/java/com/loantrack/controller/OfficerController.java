package com.loantrack.controller;

import com.loantrack.enums.ApplicationStatus;
import com.loantrack.model.Document;
import com.loantrack.model.LoanApplication;
import com.loantrack.model.LoanStatusHistory;
import com.loantrack.model.User;
import com.loantrack.repository.DocumentRepository;
import com.loantrack.repository.LoanApplicationRepository;
import com.loantrack.repository.LoanStatusHistoryRepository;
import com.loantrack.repository.UserRepository;
import com.loantrack.service.CreditScoreService;
import com.loantrack.service.EmailService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/officer")
public class OfficerController {

    private final LoanApplicationRepository applicationRepository;
    private final LoanStatusHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    // NEW INJECTIONS
    private final DocumentRepository documentRepository;
    private final CreditScoreService creditScoreService;

    public OfficerController(LoanApplicationRepository applicationRepository, 
                             LoanStatusHistoryRepository historyRepository, 
                             UserRepository userRepository,
                             EmailService emailService,
                             DocumentRepository documentRepository,
                             CreditScoreService creditScoreService) {
        this.applicationRepository = applicationRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.documentRepository = documentRepository;
        this.creditScoreService = creditScoreService;
    }

    @GetMapping("/dashboard")
    public String officerDashboard(Model model) {
        List<LoanApplication> pendingApps = applicationRepository.findAll().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
                .toList();
        
        model.addAttribute("applications", pendingApps);
        return "officer/dashboard";
    }

    @GetMapping("/application/{id}")
    public String reviewApplication(@PathVariable Long id, Model model) {
        LoanApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid application ID"));
        
        // Fetch uploaded KYC Documents
        List<Document> documents = documentRepository.findByApplication_ApplicationId(id);
        
        // Fetch baseline credit score (will default to 650 if they are a brand new user)
        int currentScore = creditScoreService.getCurrentScore(application.getUser());
        
        model.addAttribute("loanApp", application);
        model.addAttribute("documents", documents);
        model.addAttribute("creditScore", currentScore);
        
        return "officer/review-application";
    }

    @PostMapping("/application/{id}/review")
    public String submitReview(@PathVariable Long id, @RequestParam("remarks") String remarks) {
        LoanApplication application = applicationRepository.findById(id).orElseThrow();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User officer = userRepository.findByEmail(auth.getName()).orElseThrow();

        LoanStatusHistory history = new LoanStatusHistory();
        history.setApplication(application);
        history.setOldStatus(application.getStatus());
        history.setNewStatus(ApplicationStatus.UNDER_REVIEW);
        history.setChangedBy(officer);
        history.setRemarks(remarks);
        historyRepository.save(history);

        application.setStatus(ApplicationStatus.UNDER_REVIEW);
        application.setReviewedBy(officer);
        application.setRemarks(remarks);
        applicationRepository.save(application);

        emailService.sendStatusUpdateEmail(application.getUser().getEmail(), application.getUser().getFullName(), "UNDER REVIEW");

        return "redirect:/officer/dashboard?reviewed=true";
    }
}