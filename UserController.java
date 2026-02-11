package com.salarypro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired 
    private UserRepository userRepository;
    
    
    // --- 1. Static/Specific Paths first ---

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String email = loginData.get("email");
            String pass = loginData.get("password");
            
            User user = userService.login(email, pass);
            
            // Create a complete response map
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful");
            response.put("id", user.getId());        // NEEDED for fetching salary history
            response.put("name", user.getName());    // Matches 'user.name' in frontend
            response.put("email", user.getEmail());
            response.put("role", user.getRole());    // CRITICAL: Tells frontend where to go
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email, @RequestParam String newPassword) {
        try {
            String result = userService.forgotPassword(email, newPassword);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // --- 2. Dynamic ID Paths last ---

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        // This line will work now because updateUser is defined in the service
        User updatedUser = userService.updateUser(id, user); 
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully!");
    }
 // Inside your Controller
    @PutMapping("/{id}/advance")
    public ResponseEntity<User> updateAdvance(@PathVariable Long id, @RequestParam double amount) {
        User user = userService.getUserById(id);
        
        // Add the new advance amount to the existing balance
        user.setAdvanceAmount(user.getAdvanceAmount() + amount);
        
        // Save and return the updated user
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }
    
 
}