package com.loantrack.controller;

import com.loantrack.dto.LoanApplicationDTO;
import com.loantrack.enums.ApplicationStatus;
import com.loantrack.enums.DocumentType;
import com.loantrack.model.LoanApplication;
import com.loantrack.model.User;
import com.loantrack.repository.LoanApplicationRepository;
import com.loantrack.repository.UserRepository;
import com.loantrack.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/borrower")
public class LoanApplicationController {

    private final LoanApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    
    // 1. Inject the Document Service
    private final DocumentService documentService;

    public LoanApplicationController(LoanApplicationRepository applicationRepository, 
                                     UserRepository userRepository, 
                                     DocumentService documentService) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.documentService = documentService;
    }

    @GetMapping("/apply")
    public String showApplicationForm(Model model) {
        model.addAttribute("applicationDTO", new LoanApplicationDTO());
        return "borrower/apply-loan";
    }

    @PostMapping("/apply")
    public String submitApplication(@Valid @ModelAttribute("applicationDTO") LoanApplicationDTO dto, 
                                    BindingResult bindingResult, 
                                    @RequestParam("aadhaarFile") MultipartFile aadhaarFile, // 2. Accept files
                                    @RequestParam("panFile") MultipartFile panFile,         
                                    Model model) {
        
        // Check for validation errors from the DTO
        if (bindingResult.hasErrors()) {
            return "borrower/apply-loan";
        }

        // Identify who is logged in
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Map DTO to Entity
        LoanApplication application = new LoanApplication();
        application.setUser(currentUser);
        application.setLoanAmount(dto.getLoanAmount());
        application.setLoanPurpose(dto.getLoanPurpose());
        application.setTenureMonths(dto.getTenureMonths());
        application.setEmploymentType(dto.getEmploymentType());
        application.setMonthlyIncome(dto.getMonthlyIncome());
        application.setStatus(ApplicationStatus.PENDING);

        // 3. Save application FIRST so we generate an Application ID in MySQL
        LoanApplication savedApp = applicationRepository.save(application);

        // 4. Try saving the documents using the new Application ID
        try {
            documentService.saveDocument(savedApp, aadhaarFile, DocumentType.AADHAAR);
            documentService.saveDocument(savedApp, panFile, DocumentType.PAN);
        } catch (Exception e) {
            model.addAttribute("error", "File upload failed: " + e.getMessage());
            return "borrower/apply-loan";
        }

        return "redirect:/dashboard?applied=true";
    }
}