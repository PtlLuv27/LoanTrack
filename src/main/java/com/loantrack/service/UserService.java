package com.loantrack.service;

import com.loantrack.model.User;
import com.loantrack.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void updateProfile(String email, String fullName, String phone) {
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setFullName(fullName);
        user.setPhone(phone);
        userRepository.save(user);
    }

    public boolean changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow();
        
        // Check if the provided current password matches the hashed password in the DB
        if (passwordEncoder.matches(currentPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }
}