package com.sepulveda.oauth2_login.config;

import com.sepulveda.oauth2_login.model.AuthProvider;
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

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        String provider = oauthToken.getAuthorizedClientRegistrationId(); // "google" or "github"
        String providerId = oAuth2User.getName(); // Provider's user ID
        
        // Check if user already exists
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null) {
            // Create new user
            user = new UserEntity();
            user.setEmail(email);
            user.setDisplayName(name);
            user.setAvatarUrl(picture);
            user.setBio("New user registered via " + provider);
            
            userRepository.save(user);
            System.out.println("✅ New user created: " + email);
        }
        
        // Check if this auth provider is already linked
        boolean providerExists = user.getAuthProviders().stream()
            .anyMatch(ap -> ap.getProvider().equals(provider) && ap.getProviderId().equals(providerId));
        
        if (!providerExists) {
            // Link this OAuth provider to the user
            AuthProvider authProvider = new AuthProvider(user, provider, providerId);
            user.getAuthProviders().add(authProvider);
            userRepository.save(user);
            System.out.println("✅ Linked " + provider + " provider to user: " + email);
        }
        
        // Continue with the default behavior (redirect to defaultSuccessUrl)
        super.onAuthenticationSuccess(request, response, authentication);
    }
}