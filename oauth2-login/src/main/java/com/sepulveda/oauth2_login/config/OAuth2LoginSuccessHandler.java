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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.setDefaultTargetUrl("http://localhost:5173/login?callback=true");
        this.setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId();
        
        String email = getEmail(oAuth2User, provider);
        String name = getName(oAuth2User, provider);
        String picture = getPicture(oAuth2User, provider);
        String providerId = getProviderId(oAuth2User, provider);
        
        // Handle missing email from GitHub
        if (email == null || email.isEmpty()) {
            if ("github".equals(provider)) {
                String login = oAuth2User.getAttribute("login");
                email = login + "@github.user";
            } else {
                response.sendRedirect("http://localhost:5173/login?error=no_email");
                return;
            }
        }
        
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setDisplayName(name != null ? name : email.split("@")[0]);
            user.setAvatarUrl(picture);
            user.setBio("Joined via " + provider);
            
            try {
                user = userRepository.save(user);
                
                AuthProvider authProvider = new AuthProvider(user, provider, providerId);
                user.getAuthProviders().add(authProvider);
                user = userRepository.save(user);
                
            } catch (Exception e) {
                response.sendRedirect("http://localhost:5173/login?error=save_failed");
                return;
            }
        }
        
        super.onAuthenticationSuccess(request, response, authentication);
    }
    
    private String getProviderId(OAuth2User oAuth2User, String provider) {
        if ("github".equals(provider)) {
            Object id = oAuth2User.getAttribute("id");
            return id != null ? id.toString() : null;
        }
        return oAuth2User.getAttribute("sub");
    }
    
    private String getEmail(OAuth2User oAuth2User, String provider) {
        return oAuth2User.getAttribute("email");
    }
    
    private String getName(OAuth2User oAuth2User, String provider) {
        if ("github".equals(provider)) {
            String name = oAuth2User.getAttribute("name");
            if (name == null || name.isEmpty()) {
                name = oAuth2User.getAttribute("login");
            }
            return name;
        }
        return oAuth2User.getAttribute("name");
    }
    
    private String getPicture(OAuth2User oAuth2User, String provider) {
        if ("github".equals(provider)) {
            return oAuth2User.getAttribute("avatar_url");
        }
        return oAuth2User.getAttribute("picture");
    }
}