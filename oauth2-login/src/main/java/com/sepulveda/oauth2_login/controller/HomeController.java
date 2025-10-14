package com.sepulveda.oauth2_login.controller;

import com.sepulveda.oauth2_login.model.UserEntity;
import com.sepulveda.oauth2_login.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal, Model model) {
        System.out.println("=== HOME CONTROLLER CALLED ===");
        
        if (principal != null) {
            String email = principal.getAttribute("email");
            String name = principal.getAttribute("name");
            String picture = principal.getAttribute("picture");
            
            System.out.println("Authenticated user email: " + email);
            System.out.println("Authenticated user name: " + name);
            
            // Check if email is null (GitHub sometimes doesn't provide email)
            if (email == null || email.isEmpty()) {
                // Try to get email from GitHub-specific attribute
                email = principal.getAttribute("login") + "@github.user";
                System.out.println("Email was null, using GitHub login: " + email);
            }
            
            // Auto-create user if doesn't exist (workaround)
            UserEntity user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                System.out.println("User not found in DB - creating now...");
                user = new UserEntity();
                user.setEmail(email);
                user.setDisplayName(name != null ? name : "GitHub User");
                user.setAvatarUrl(picture != null ? picture : principal.getAttribute("avatar_url"));
                user.setBio("User created from " + (email.contains("@github.user") ? "GitHub" : "Google"));
                userRepository.save(user);
                System.out.println("✅ User created!");
            }
            
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("picture", picture != null ? picture : principal.getAttribute("avatar_url"));
            return "home";
        }
        
        System.out.println("No principal - redirecting to login");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}