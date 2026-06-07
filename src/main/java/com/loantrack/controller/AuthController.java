package com.loantrack.controller;

import com.loantrack.enums.Role;
import com.loantrack.model.User;
import com.loantrack.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email is already registered!");
            return "register";
        }
        
        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // For development simplicity, let's default all registrations to BORROWER
        user.setRole(Role.BORROWER);
        
        userRepository.save(user);
        return "redirect:/login?registered";
    }

}