package com.sepulveda.oauth2_login.controller;

import com.sepulveda.oauth2_login.model.UserEntity;
import com.sepulveda.oauth2_login.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String getProfile(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null) {
            return "redirect:/";
        }

        String email = principal.getAttribute("email");
        
        // Handle GitHub users without email
        if (email == null || email.isEmpty()) {
            email = principal.getAttribute("login") + "@github.user";
        }
        
        UserEntity user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String bio,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/";
        }

        String email = principal.getAttribute("email");
        
        // Handle GitHub users without email
        if (email == null || email.isEmpty()) {
            email = principal.getAttribute("login") + "@github.user";
        }
        
        try {
            userService.updateProfileByEmail(email, displayName, bio);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
        }

        return "redirect:/profile";
    }
}