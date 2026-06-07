package com.loantrack.scheduler;

import com.loantrack.enums.PaymentStatus;
import com.loantrack.model.EMISchedule;
import com.loantrack.repository.EMIScheduleRepository;
import com.loantrack.service.EmailService;
import com.loantrack.service.PenaltyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class EMISchedulerJob {

    private final EMIScheduleRepository emiRepository;
    private final PenaltyService penaltyService;
    private final EmailService emailService;

    public EMISchedulerJob(EMIScheduleRepository emiRepository, 
                           PenaltyService penaltyService, 
                           EmailService emailService) {
        this.emiRepository = emiRepository;
        this.penaltyService = penaltyService;
        this.emailService = emailService;
    }

    // Cron pattern: Seconds Minutes Hours DayOfMonth Month DayOfWeek
    // "0 0 0 * * *" = Runs at Exactly Midnight every single day
    @Scheduled(cron = "0 0 0 * * *")
    public void applyPenalties() {
        System.out.println("🤖 [MIDNIGHT CRON JOB] Scanning for overdue EMIs...");
        
        List<EMISchedule> overdueEMIs = emiRepository
                .findByPaymentStatusAndDueDateBefore(PaymentStatus.UPCOMING, LocalDate.now());

        for (EMISchedule emi : overdueEMIs) {
            long overdueDays = ChronoUnit.DAYS.between(emi.getDueDate(), LocalDate.now());
            
            // We give the borrower a 3-day grace period before slapping them with a penalty
            if (overdueDays > 3) {
                BigDecimal penalty = penaltyService.calculate(emi, overdueDays);
                
                emi.setPenaltyAmount(penalty);
                emi.setPaymentStatus(PaymentStatus.MISSED);
                emiRepository.save(emi);
                
                System.out.println("⚠️ Applied ₹" + penalty + " penalty to EMI ID: " + emi.getEmiId());
                
                // (In Week 4, we will also call CreditScoreService.deductPoints() right here)
            }
        }
    }

    // "0 0 9 * * *" = Runs at Exactly 9:00 AM every single day
    @Scheduled(cron = "0 0 9 * * *")
    public void sendEMIReminders() {
        System.out.println("🤖 [9AM CRON JOB] Sending EMI reminders...");
        
        LocalDate threeDaysLater = LocalDate.now().plusDays(3);
        List<EMISchedule> upcomingEMIs = emiRepository
                .findByPaymentStatusAndDueDate(PaymentStatus.UPCOMING, threeDaysLater);

        for (EMISchedule emi : upcomingEMIs) {
            String toEmail = emi.getLoan().getUser().getEmail();
            String name = emi.getLoan().getUser().getFullName();
            
            emailService.sendStatusUpdateEmail(toEmail, name, 
                    "Reminder: Your EMI of ₹" + emi.getEmiAmount() + " is due in 3 days on " + emi.getDueDate());
        }
    }
}