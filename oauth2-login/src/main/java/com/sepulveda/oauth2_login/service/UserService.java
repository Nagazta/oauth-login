package com.sepulveda.oauth2_login.service;

import com.sepulveda.oauth2_login.model.UserEntity;
import com.sepulveda.oauth2_login.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Transactional
    public UserEntity updateProfile(Long userId, String displayName, String bio) {
        UserEntity user = getUserById(userId);
        
        if (displayName != null && !displayName.trim().isEmpty()) {
            user.setDisplayName(displayName.trim());
        }
        
        if (bio != null) {
            user.setBio(bio.trim());
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public UserEntity updateProfileByEmail(String email, String displayName, String bio) {
        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (displayName != null && !displayName.trim().isEmpty()) {
            user.setDisplayName(displayName.trim());
        }
        
        if (bio != null) {
            user.setBio(bio.trim());
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public UserEntity createUser(UserEntity user) {
        return userRepository.save(user);
    }
    
    @Transactional
    public UserEntity updateUser(Long id, String email, String displayName, String bio) {
        UserEntity user = getUserById(id);
        
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email.trim());
        }
        
        if (displayName != null && !displayName.trim().isEmpty()) {
            user.setDisplayName(displayName.trim());
        }
        
        if (bio != null) {
            user.setBio(bio.trim());
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}