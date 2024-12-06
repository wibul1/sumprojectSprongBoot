package com.example.project.controller;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.project.model.UserModel;
import com.example.project.model.UserTokenModel;
import com.example.project.repository.UserRepository;
import com.example.project.repository.UserTokenRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTokenRepository userTokenRepository;

    // Secret key for JWT - in a real application, store this securely
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token expiration time (7 days)
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7 days in milliseconds

    // API สำหรับการสมัคร (Register)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserModel user) {
        try {
            // Check if username already exists
            if (userRepository.existsByUsername(user.getUsername())) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Username already exists");
            }

            // Save the user
            UserModel savedUser = userRepository.save(user);
            
            // Remove password before returning
            savedUser.setPassword(null);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserModel loginRequest) {
        try {
            // Find user by username
            UserModel user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate password
            if (!user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid password");
            }

            // Generate JWT token
            String tokenString = generateJwtToken(user);

            // Create token model
            UserTokenModel tokenModel = new UserTokenModel();
            tokenModel.setUserId(user.getId());
            tokenModel.setToken(tokenString);
            tokenModel.setExpiresAt(LocalDateTime.now().plusDays(7));

            // Save token to database
            userTokenRepository.save(tokenModel);

            // Add token to user's tokens
            user.addToken(tokenModel);
            userRepository.save(user);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("token", tokenString);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            // Remove "Bearer " if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Find and invalidate the token
            UserTokenModel userToken = userTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

            userToken.setActive(false);
            userTokenRepository.save(userToken);

            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Logout failed: " + e.getMessage());
        }
    }

    // Method to generate JWT token
    private String generateJwtToken(UserModel user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
            .setSubject(user.getUsername())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact();
    }
}