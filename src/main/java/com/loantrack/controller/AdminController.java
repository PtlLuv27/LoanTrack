package com.loantrack.controller;

import com.loantrack.enums.ApplicationStatus;
import com.loantrack.enums.LoanStatus;
import com.loantrack.model.*;
import com.loantrack.repository.*;
import com.loantrack.service.CreditScoreService;
import com.loantrack.service.EmailService;
import com.loantrack.service.LoanService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final LoanApplicationRepository applicationRepository;
    private final LoanStatusHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final LoanService loanService;
    private final CreditScoreService creditScoreService;
    private final LoanRepository loanRepository;
    private final PaymentRepository paymentRepository;
    private final EMIScheduleRepository emiRepository;

    public AdminController(LoanApplicationRepository applicationRepository,
                           LoanStatusHistoryRepository historyRepository,
                           UserRepository userRepository,
                           EmailService emailService,
                           LoanService loanService,
                           CreditScoreService creditScoreService,
                           LoanRepository loanRepository,
                           PaymentRepository paymentRepository,
                           EMIScheduleRepository emiRepository) {
        this.applicationRepository = applicationRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.loanService = loanService;
        this.creditScoreService = creditScoreService;
        this.loanRepository = loanRepository;
        this.paymentRepository = paymentRepository;
        this.emiRepository = emiRepository;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // 1. Fetch the approval queue
        List<LoanApplication> underReviewApps = applicationRepository.findAll().stream()
                .filter(app -> app.getStatus() == ApplicationStatus.UNDER_REVIEW)
                .toList();

        // 2. Fetch Active Loans for the new table
        List<Loan> activeLoans = loanRepository.findByLoanStatus(LoanStatus.ACTIVE);
        
        // 3. Fetch Bank Analytics
        BigDecimal totalDisbursed = loanRepository.sumTotalDisbursed();
        totalDisbursed = totalDisbursed != null ? totalDisbursed : BigDecimal.ZERO;

        BigDecimal totalCollected = paymentRepository.sumTotalCollected();
        totalCollected = totalCollected != null ? totalCollected : BigDecimal.ZERO;

        BigDecimal totalOutstanding = loanRepository.sumTotalOutstanding();
        totalOutstanding = totalOutstanding != null ? totalOutstanding : BigDecimal.ZERO;

        long activeLoansCount = loanRepository.countByLoanStatus(LoanStatus.ACTIVE);

        // 4. Add to model
        model.addAttribute("applications", underReviewApps);
        model.addAttribute("activeLoans", activeLoans);
        model.addAttribute("totalDisbursed", totalDisbursed);
        model.addAttribute("totalCollected", totalCollected);
        model.addAttribute("totalOutstanding", totalOutstanding);
        model.addAttribute("activeLoansCount", activeLoansCount);

        return "admin/dashboard";
    }

    // NEW ROUTE: View Borrower Details & Transaction History
    @GetMapping("/loan/{id}")
    public String viewLoanLedger(@PathVariable Long id, Model model) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Loan ID"));
        List<Payment> payments = paymentRepository.findByLoan_LoanIdOrderByPaymentDateDesc(id);
        List<EMISchedule> schedule = emiRepository.findByLoan_LoanIdOrderByEmiNumberAsc(id);
        
        model.addAttribute("loan", loan);
        model.addAttribute("payments", payments);
        model.addAttribute("schedule", schedule);
        
        return "admin/loan-details";
    }

    @PostMapping("/approve/{id}")
    public String approveLoan(@PathVariable Long id) {
        LoanApplication application = applicationRepository.findById(id).orElseThrow();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userRepository.findByEmail(auth.getName()).orElseThrow();

        application.setStatus(ApplicationStatus.APPROVED);
        application.setApprovedBy(admin);
        application.setRemarks("Final Approval granted by Admin.");
        applicationRepository.save(application);

        LoanStatusHistory history = new LoanStatusHistory();
        history.setApplication(application);
        history.setOldStatus(ApplicationStatus.UNDER_REVIEW);
        history.setNewStatus(ApplicationStatus.APPROVED);
        history.setChangedBy(admin);
        history.setRemarks("Approved by Admin");
        historyRepository.save(history);

        loanService.createLoanFromApplication(application);
        creditScoreService.initializeScore(application.getUser());
        emailService.sendStatusUpdateEmail(application.getUser().getEmail(), application.getUser().getFullName(), "APPROVED");

        return "redirect:/admin/dashboard?approved=true";
    }

    @PostMapping("/reject/{id}")
    public String rejectLoan(@PathVariable Long id, @RequestParam("reason") String reason) {
        LoanApplication application = applicationRepository.findById(id).orElseThrow();
        User admin = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();

        application.setStatus(ApplicationStatus.REJECTED);
        application.setApprovedBy(admin);
        applicationRepository.save(application);

        LoanStatusHistory history = new LoanStatusHistory();
        history.setApplication(application);
        history.setOldStatus(ApplicationStatus.UNDER_REVIEW);
        history.setNewStatus(ApplicationStatus.REJECTED);
        history.setChangedBy(admin);
        history.setRemarks(reason);
        historyRepository.save(history);

        emailService.sendStatusUpdateEmail(application.getUser().getEmail(), application.getUser().getFullName(), "REJECTED. Reason: " + reason);

        return "redirect:/admin/dashboard?rejected=true";
    }
    @GetMapping("/export/loans")
    public void exportActiveLoansToCSV(jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        // Set the response headers to trigger a file download
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"active_portfolios_report.csv\"");

        List<Loan> activeLoans = loanRepository.findByLoanStatus(LoanStatus.ACTIVE);

        // Initialize the Indian regional formatter to handle Lakhs and Crores placement natively
        java.text.NumberFormat regionalFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.of("en", "IN"));

        // Write the CSV data
        try (java.io.PrintWriter writer = response.getWriter()) {
            // Write the CSV Header Row
            writer.println("Loan ID,Borrower Name,Disbursed Amount,Outstanding Balance,Next EMI Date");

            // Loop through the data and write each row
            for (Loan loan : activeLoans) {
                writer.println(String.format("%d,%s,\"%s\",\"%s\",%s",
                        loan.getLoanId(),
                        loan.getUser().getFullName(),
                        regionalFormat.format(loan.getPrincipalAmount()),
                        regionalFormat.format(loan.getOutstandingBalance()),
                        loan.getEndDate().toString()
                ));
            }
        }
    }
}