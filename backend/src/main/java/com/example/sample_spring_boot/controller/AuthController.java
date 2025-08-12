package com.example.sample_spring_boot.controller;

import com.example.sample_spring_boot.entity.User;
import com.example.sample_spring_boot.repository.UserRepository;
import com.example.sample_spring_boot.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.email(),
                    loginRequest.password()
                )
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.email());
            final String jwt = jwtUtil.generateToken(userDetails);
            
            // Update last login time
            User user = userRepository.findByEmail(loginRequest.email()).orElse(null);
            if (user != null) {
                user.setCreatedAt(System.currentTimeMillis()); // This could be lastLogin field
                userRepository.save(user);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("email", userDetails.getUsername());
            response.put("fullName", user.getFullName());
            response.put("message", "Login successful");
            
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid email or password");
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.existsByEmail(registerRequest.email())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email already exists");
            return ResponseEntity.badRequest().body(error);
        }
        
        // Create new user
        User user = new User(
            registerRequest.email(),
            passwordEncoder.encode(registerRequest.password()),
            registerRequest.fullName()
        );
        
        User savedUser = userRepository.save(user);
        
        // Generate JWT token for the new user
        final UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("email", savedUser.getEmail());
        response.put("fullName", savedUser.getFullName());
        response.put("message", "Registration successful");
        response.put("id", savedUser.getId());
        response.put("createdAt", savedUser.getCreatedAt());
        return ResponseEntity.ok(response);
    }
    
    // Inner classes for request bodies
    public record LoginRequest(String email, String password) {
    }

    public record RegisterRequest(String email, String password, String fullName) {
    }
}
