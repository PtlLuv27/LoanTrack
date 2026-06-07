package com.loantrack.controller;

import com.loantrack.model.EMISchedule;
import com.loantrack.model.Loan;
import com.loantrack.model.User;
import com.loantrack.service.CreditScoreService;
import com.loantrack.service.PdfService;
import com.loantrack.repository.CreditScoreRepository;
import com.loantrack.repository.EMIScheduleRepository;
import com.loantrack.repository.LoanRepository;
import com.loantrack.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Controller
public class BorrowerController {

    private final LoanRepository loanRepository;
    private final EMIScheduleRepository emiRepository;
    private final UserRepository userRepository;
    private final CreditScoreService creditScoreService;
    private final PdfService pdfService;
    private final CreditScoreRepository creditScoreRepository;

    public BorrowerController(LoanRepository loanRepository, 
                              EMIScheduleRepository emiRepository, 
                              UserRepository userRepository,
                              CreditScoreService creditScoreService,
                              CreditScoreRepository creditScoreRepository,
                              PdfService pdfService ) {
        this.loanRepository = loanRepository;
        this.emiRepository = emiRepository;
        this.userRepository = userRepository;
        this.creditScoreService = creditScoreService;
        this.pdfService = pdfService;
        this.creditScoreRepository = creditScoreRepository;
    }

    // This replaces the old temporary dashboard
    @GetMapping("/dashboard")
    public String borrowerDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();

        List<Loan> myLoans = loanRepository.findByUser_UserId(user.getUserId());
        model.addAttribute("loans", myLoans);
        model.addAttribute("userName", user.getFullName());
        
        return "borrower/dashboard"; 
    }

    @GetMapping("/borrower/loan/{id}/schedule")
    public String viewSchedule(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid loan ID"));
        
        // Security check: Prevent users from typing random IDs and viewing other people's loans
        if (!loan.getUser().getUserId().equals(user.getUserId())) {
            return "redirect:/dashboard?error=unauthorized";
        }

        List<EMISchedule> schedule = emiRepository.findByLoan_LoanIdOrderByEmiNumberAsc(id);
        
        model.addAttribute("loan", loan);
        model.addAttribute("schedule", schedule);
        
        return "borrower/emi-schedule";
    }

    @GetMapping("/borrower/credit-score")
    public String viewCreditScore(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();

        int currentScore = creditScoreService.getCurrentScore(user);
        List<com.loantrack.model.CreditScore> history = creditScoreRepository.findByUser_UserIdOrderByRecordedAtDesc(user.getUserId());

        model.addAttribute("currentScore", currentScore);
        model.addAttribute("history", history);
        return "borrower/credit-score";
    }
    @GetMapping("/borrower/loan/{id}/statement/pdf")
    public org.springframework.http.ResponseEntity<byte[]> downloadPdfStatement(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();

        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid loan ID"));

        // Security check
        if (!loan.getUser().getUserId().equals(user.getUserId())) {
            return org.springframework.http.ResponseEntity.status(403).build();
        }

        // Generate the file
        byte[] pdfBytes = pdfService.generateLoanStatement(loan);

        // Tell the browser to download it as a PDF file
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Loan_Statement_" + loan.getLoanId() + ".pdf");

        return org.springframework.http.ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }
}