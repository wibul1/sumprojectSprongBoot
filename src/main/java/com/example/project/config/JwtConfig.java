package com.example.project.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7 days

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public long getExpirationTime() {
        return EXPIRATION_TIME;
    }
}
