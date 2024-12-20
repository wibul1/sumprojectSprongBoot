package com.example.project.controller;

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

import com.example.project.config.JwtConfig;
import com.example.project.model.UserModel;
import com.example.project.model.UserTokenModel;
import com.example.project.repository.UserRepository;
import com.example.project.repository.UserTokenRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private JwtConfig jwtConfig;

    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7 days in milliseconds

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserModel user) {
        try {
            if (userRepository.existsByUsername(user.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
            }

            UserModel savedUser = userRepository.save(user);
            savedUser.setPassword(null);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
public ResponseEntity<?> loginUser(@RequestBody UserModel loginRequest) {
    try {
        // ค้นหาผู้ใช้ในฐานข้อมูล
        UserModel user = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        // ตรวจสอบรหัสผ่าน
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        // สร้าง JWT
        String tokenString = generateJwtToken(user);

        // สร้าง UserTokenModel และเก็บในฐานข้อมูล
        UserTokenModel tokenModel = new UserTokenModel();
        tokenModel.setUserId(user.getId());
        tokenModel.setToken(tokenString);
        tokenModel.setExpiresAt(LocalDateTime.now().plusDays(7));

        userTokenRepository.save(tokenModel);

        // เก็บ Token ใน User
        user.addToken(tokenModel);
        userRepository.save(user);

        // ส่ง response กลับไปพร้อมกับ token
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("token", tokenString);

        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
    }
}



@PostMapping("/logout")
public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
    try {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);  // ลบคำว่า "Bearer " ออก
        }

        // ค้นหาโทเค็นในฐานข้อมูล
        UserTokenModel userToken = userTokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Token not found"));

        // ตั้งค่าให้โทเค็นเป็นไม่สามารถใช้งานได้
        userToken.setActive(false);  // หรือถ้าคุณใช้ expire date ก็ให้ตั้งวันที่หมดอายุ
        userTokenRepository.save(userToken);  // บันทึกการเปลี่ยนแปลง

        return ResponseEntity.ok("Logout successful");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Logout failed: " + e.getMessage());
    }
}


    private String generateJwtToken(UserModel user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
            .setSubject(user.getUsername())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(jwtConfig.getSecretKey(), SignatureAlgorithm.HS256)
            .compact();
    }
}
