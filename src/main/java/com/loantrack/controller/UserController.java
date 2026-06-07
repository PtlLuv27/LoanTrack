package com.loantrack.controller;

import com.loantrack.model.User;
import com.loantrack.repository.UserRepository;
import com.loantrack.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam("phone") String phone,
                                RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userService.updateProfile(auth.getName(), fullName, phone);
        
        // RedirectAttributes allows us to pass success messages after a redirect
        redirectAttributes.addFlashAttribute("successMsg", "Profile details updated successfully!");
        return "redirect:/profile";
    }

    @PostMapping("/profile/password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean success = userService.changePassword(auth.getName(), currentPassword, newPassword);
        
        if (success) {
            redirectAttributes.addFlashAttribute("successMsg", "Password changed successfully! Please use it on your next login.");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Incorrect current password. Please try again.");
        }
        
        return "redirect:/profile";
    }
}