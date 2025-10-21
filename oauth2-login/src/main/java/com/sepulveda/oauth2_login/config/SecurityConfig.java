package com.sepulveda.oauth2_login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        System.out.println("=== SecurityConfig Constructor CALLED ===");
        System.out.println("Handler injected: " + oAuth2LoginSuccessHandler);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println(">>> CONFIGURING SECURITY <<<");
        
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/h2-console/**",
                    "/error",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**"
                ).permitAll()
                .requestMatchers("/api/user/me", "/api/user/profile").authenticated()
                .requestMatchers("/api/user/all", "/api/user/{id}").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> {
                System.out.println(">>> SETTING OAUTH2 SUCCESS HANDLER <<<");
                oauth2.successHandler(oAuth2LoginSuccessHandler);  // ← ONLY THIS LINE!
            })
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("http://localhost:5173/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/logout", "/api/user/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );
        
        System.out.println(">>> SECURITY CONFIGURED <<<");
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}