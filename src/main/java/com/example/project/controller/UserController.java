package com.example.project.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.project.model.UserModel;
import com.example.project.repository.UserRepository;
import com.example.project.service.JwtService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Validate token first
            if (!jwtService.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid token");
            }

            String username = jwtService.extractUsername(token);
            UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Error: " + e.getMessage());
        }
    }
}





        // @GetMapping("/profile")
        // public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        //     // ตัวอย่างการประมวลผล
        //     Map<String, String> profile = Map.of(
        //         "firstName", "John",
        //         "lastName", "Doe",
        //         "email", "john.doe@example.com"
        //     );
        //     return ResponseEntity.ok(profile);
        // }


