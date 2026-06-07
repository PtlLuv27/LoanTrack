package com.loantrack.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendStatusUpdateEmail(String toEmail, String borrowerName, String newStatus) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("LoanTrack: Application Status Update");
        message.setText("Dear " + borrowerName + ",\n\n" +
                "Your loan application status has been updated to: " + newStatus + ".\n\n" +
                "Log in to your dashboard to view more details.\n\n" +
                "Thank you,\nThe LoanTrack Team");
        
        // Wrap in a try-catch so the app doesn't crash if your SMTP credentials aren't set up yet
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}