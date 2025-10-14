package com.sepulveda.oauth2_login.config;

import com.sepulveda.oauth2_login.model.UserEntity;
import com.sepulveda.oauth2_login.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        
        System.out.println("=== OAuth2 Login Handler Called ===");
        
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        String provider = oauthToken.getAuthorizedClientRegistrationId();
        
        System.out.println("Email: " + email);
        System.out.println("Name: " + name);
        System.out.println("Provider: " + provider);
        
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null) {
            System.out.println("Creating new user...");
            user = new UserEntity();
            user.setEmail(email);
            user.setDisplayName(name);
            user.setAvatarUrl(picture);
            user.setBio("New user registered via " + provider);
            
            user = userRepository.save(user);
            System.out.println("✅ New user created with ID: " + user.getId());
        } else {
            System.out.println("✅ User already exists with ID: " + user.getId());
        }
        
        System.out.println("=== End OAuth2 Login Handler ===");
        
        super.onAuthenticationSuccess(request, response, authentication);
    }
}