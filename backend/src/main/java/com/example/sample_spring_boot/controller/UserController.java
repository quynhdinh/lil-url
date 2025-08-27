package com.example.sample_spring_boot.controller;

import com.example.sample_spring_boot.entity.User;
import com.example.sample_spring_boot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);

        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);

        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            CreateUserResponse response = new CreateUserResponse(false, "User with this email already exists", null, null, null);
            return ResponseEntity.badRequest().body(response);
        }
        
        // Create a new user
        User user = new User(request.email(), request.password(), request.fullName());
        User savedUser = userRepository.save(user);

        CreateUserResponse response = new CreateUserResponse(true, "User created successfully", savedUser.getId(), savedUser.getEmail(), savedUser.getFullName());
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    public record CreateUserRequest(String email, String password, String fullName) {
    }
    
    public record CreateUserResponse(boolean success, String message, Integer userId, String email, String fullName) {
    }
        
}