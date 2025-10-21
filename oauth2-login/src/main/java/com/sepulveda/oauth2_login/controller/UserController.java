package com.sepulveda.oauth2_login.controller;

import com.sepulveda.oauth2_login.model.UserEntity;
import com.sepulveda.oauth2_login.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import com.sepulveda.oauth2_login.DTO.UserDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationData) {
        String email = registrationData.get("email");
        String displayName = registrationData.get("displayName");

        if (email == null || email.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email is required");
            return ResponseEntity.status(400).body(error);
        }

        if (userService.getUserByEmail(email).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User already exists with this email");
            return ResponseEntity.status(409).body(error);
        }

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setDisplayName(displayName != null ? displayName : email.split("@")[0]);
        user.setBio("New user");

        try {
            user = userService.createUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful! You can now login with OAuth.");
            response.put("userId", user.getId());
            
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = userService.getUserByEmail(email).isPresent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Not authenticated");
            return ResponseEntity.status(401).body(error);
        }

        String email = principal.getAttribute("email");

        // Handle GitHub users without email
        if (email == null || email.isEmpty()) {
            String login = principal.getAttribute("login");
            if (login != null) {
                email = login + "@github.user";
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email not found in user profile");
                return ResponseEntity.status(400).body(error);
            }
        }

        final String finalEmail = email;

        return userService.getUserByEmail(finalEmail)
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(toDTO(user)))
                .orElseGet(() -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "User not found in database");
                    error.put("email", finalEmail);
                    return ResponseEntity.status(404).body(error);
                });
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestBody Map<String, String> updates) {

        if (principal == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Not authenticated");
            return ResponseEntity.status(401).body(error);
        }

        String email = principal.getAttribute("email");
        
        // Handle GitHub users without email - same as /me endpoint
        if (email == null || email.isEmpty()) {
            String login = principal.getAttribute("login");
            if (login != null) {
                email = login + "@github.user";
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email not found");
                return ResponseEntity.status(400).body(error);
            }
        }

        String displayName = updates.get("displayName");
        String bio = updates.get("bio");

        try {
            UserEntity updatedUser = userService.updateProfileByEmail(email, displayName, bio);
            return ResponseEntity.ok(toDTO(updatedUser));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update profile: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    private UserDTO toDTO(UserEntity user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setDisplayName(user.getDisplayName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        return dto;
    }


    @GetMapping("/all")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserEntity user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(404).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserEntity user) {
        try {
            UserEntity createdUser = userService.createUser(user);
            return ResponseEntity.status(201).body(createdUser);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create user");
            return ResponseEntity.status(400).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates) {

        String displayName = updates.get("displayName");
        String bio = updates.get("bio");
        String email = updates.get("email");

        try {
            UserEntity updatedUser = userService.updateUser(id, email, displayName, bio);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(404).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            Map<String, String> success = new HashMap<>();
            success.put("message", "User deleted successfully");
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(404).body(error);
        }
    }
}