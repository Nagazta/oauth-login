package com.sepulveda.oauth2_login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        System.out.println("=== SecurityConfig Constructor - Handler injected: " + oAuth2LoginSuccessHandler);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("=== Configuring SecurityFilterChain with handler: " + oAuth2LoginSuccessHandler);
        
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/login", "/error").permitAll()
                .requestMatchers("/", "/profile").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> {
                System.out.println("=== Configuring OAuth2 Login ===");
                oauth2
                    .successHandler(oAuth2LoginSuccessHandler)
                    .defaultSuccessUrl("/", true)
                    .loginPage("/login");
            })
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );
        
        return http.build();
    }
}