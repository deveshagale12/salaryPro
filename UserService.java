package com.salarypro;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 1. REGISTER: Optimized to handle Role strictly
    public User registerUser(User user) {
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // Logic fix: Ensure we don't overwrite if the frontend sends "ADMIN"
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("USER");
        } else {
            // Standardize to uppercase to avoid "admin" vs "ADMIN" mismatch
            user.setRole(user.getRole().toUpperCase());
        }
        
        return userRepository.save(user);
    }

    // 2. GET BY ID: Safe retrieval
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // 3. ADVANCE LOGIC: Fixed to prevent NullPointer and handle math correctly
    @Transactional // Ensures database consistency
    public User giveAdvance(Long userId, double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Add current advance to new advance
        double currentAdvance = user.getAdvanceAmount();
        user.setAdvanceAmount(currentAdvance + amount);
        
        return userRepository.save(user);
    }

    // 4. LOGIN LOGIC: Returning full object for Frontend to store Role
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not registered!"));

        if (user.getPassword().equals(password)) {
            return user; // Important: Returns the Role and ID for the Dashboard redirect
        } else {
            throw new RuntimeException("Incorrect Password!");
        }
    }

    // 5. GET ALL USERS: For Admin Table
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 6. DELETE USER
    public void deleteUser(Long id) {
        if(!userRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: User not found");
        }
        userRepository.deleteById(id);
    }

    // 7. FORGOT PASSWORD
    public String forgotPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No user found with this email"));
        
        user.setPassword(newPassword);
        userRepository.save(user);
        return "Password updated successfully!";
    }
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);
        
        existingUser.setName(userDetails.getName());
        existingUser.setMobile(userDetails.getMobile());
        existingUser.setRole(userDetails.getRole());
        
        return userRepository.save(existingUser);
    }
    
    
}