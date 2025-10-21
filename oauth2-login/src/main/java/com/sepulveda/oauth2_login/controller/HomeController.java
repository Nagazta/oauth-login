package com.sepulveda.oauth2_login.controller;

import com.sepulveda.oauth2_login.model.UserEntity;
import com.sepulveda.oauth2_login.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/user/account")
    public UserEntity getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return null;
        }

        String email = principal.getAttribute("email");
        if (email == null || email.isEmpty()) {
            email = principal.getAttribute("login") + "@github.user";
        }

        return userRepository.findByEmail(email).orElse(null);
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
