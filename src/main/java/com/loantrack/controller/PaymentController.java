package com.loantrack.controller;

import com.loantrack.enums.PaymentMode;
import com.loantrack.model.EMISchedule;
import com.loantrack.model.User;
import com.loantrack.repository.EMIScheduleRepository;
import com.loantrack.repository.UserRepository;
import com.loantrack.service.PaymentService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentController {

    private final EMIScheduleRepository emiRepository;
    private final PaymentService paymentService;
    private final UserRepository userRepository;

    public PaymentController(EMIScheduleRepository emiRepository, 
                             PaymentService paymentService, 
                             UserRepository userRepository) {
        this.emiRepository = emiRepository;
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/borrower/pay/{emiId}")
    public String showPaymentScreen(@PathVariable Long emiId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();

        EMISchedule emi = emiRepository.findById(emiId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid EMI ID"));

        // Security check
        if (!emi.getLoan().getUser().getUserId().equals(user.getUserId())) {
            return "redirect:/dashboard?error=unauthorized";
        }

        model.addAttribute("emi", emi);
        model.addAttribute("totalPayable", emi.getEmiAmount().add(emi.getPenaltyAmount()));
        return "borrower/make-payment";
    }

    @PostMapping("/borrower/pay/{emiId}")
    public String processPayment(@PathVariable Long emiId, @RequestParam("paymentMode") PaymentMode paymentMode) {
        try {
            paymentService.processEMIPayment(emiId, paymentMode);
            // Get the loan ID so we can redirect back to the schedule
            Long loanId = emiRepository.findById(emiId).get().getLoan().getLoanId();
            return "redirect:/borrower/loan/" + loanId + "/schedule?paymentSuccess=true";
        } catch (Exception e) {
            return "redirect:/dashboard?error=payment_failed";
        }
    }
}