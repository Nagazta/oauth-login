package com.sepulveda.oauth2_login.controller;

import com.sepulveda.oauth2_login.model.UserEntity;
import com.sepulveda.oauth2_login.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getProfile")
    public UserEntity getProfile(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            throw new RuntimeException("Not authenticated");
        }

        String email = principal.getAttribute("email");
        if (email == null) {
            throw new RuntimeException("Email not found in OAuth2 profile");
        }

        return userService
                .getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/updateProfile")
    public UserEntity updateProfile(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestBody Map<String, String> updates) {

        if (principal == null) {
            throw new RuntimeException("Not authenticated");
        }

        String email = principal.getAttribute("email");
        if (email == null) {
            throw new RuntimeException("Email not found in OAuth2 profile");
        }

        String displayName = updates.get("displayName");
        String bio = updates.get("bio");

        return userService.updateProfileByEmail(email, displayName, bio);
    }


    @GetMapping("/getUserAll")
    public List<UserEntity> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/getUser/{id}")
    public UserEntity getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/add")
    public UserEntity createUser(@RequestBody UserEntity user) {
        return userService.createUser(user);
    }

    @PutMapping("/update/{id}")
    public UserEntity updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates) {

        String displayName = updates.get("displayName");
        String bio = updates.get("bio");
        String email = updates.get("email");

        return userService.updateUser(id, email, displayName, bio);
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User with ID " + id + " deleted successfully.";
    }
}
