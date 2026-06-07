package com.loantrack.service;

import com.loantrack.model.CreditScore;
import com.loantrack.model.EMISchedule;
import com.loantrack.model.User;
import com.loantrack.repository.CreditScoreRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class CreditScoreService {

    private final CreditScoreRepository scoreRepository;

    public CreditScoreService(CreditScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public int getCurrentScore(User user) {
        return scoreRepository.findTopByUser_UserIdOrderByRecordedAtDesc(user.getUserId())
                .map(CreditScore::getScoreValue)
                .orElse(650); // Default starting score per PRD
    }

    public void initializeScore(User user) {
        if (scoreRepository.findTopByUser_UserIdOrderByRecordedAtDesc(user.getUserId()).isEmpty()) {
            saveScoreChange(user, 650, 0, "Initial base score upon first loan approval", null);
        }
    }

    public void updateScoreOnPayment(User user, EMISchedule emi) {
        int currentScore = getCurrentScore(user);
        int change = 0;
        String reason = "";

        long daysLate = ChronoUnit.DAYS.between(emi.getDueDate(), LocalDate.now());

        if (daysLate <= 0) {
            change = 5;
            reason = "On-time EMI payment";
        } else if (daysLate <= 7) {
            change = -5;
            reason = "Late payment (1-7 days)";
        } else if (daysLate <= 30) {
            change = -15;
            reason = "Late payment (8-30 days)";
        } else {
            change = -25;
            reason = "Severe delay (30+ days)";
        }

        saveScoreChange(user, currentScore + change, change, reason, emi);
    }

    public void deductForMissedEMI(User user, EMISchedule emi) {
        int currentScore = getCurrentScore(user);
        saveScoreChange(user, currentScore - 30, -30, "Missed EMI deadline entirely", emi);
    }

    private void saveScoreChange(User user, int newScore, int changeAmount, String reason, EMISchedule emi) {
        // Cap the score between 300 and 900
        newScore = Math.max(300, Math.min(newScore, 900));

        CreditScore scoreEntry = new CreditScore();
        scoreEntry.setUser(user);
        scoreEntry.setScoreValue(newScore);
        scoreEntry.setChangeAmount(changeAmount);
        scoreEntry.setChangeReason(reason);
        scoreEntry.setRelatedEmi(emi);
        
        scoreRepository.save(scoreEntry);
    }
}