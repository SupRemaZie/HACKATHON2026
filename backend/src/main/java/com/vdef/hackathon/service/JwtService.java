package com.vdef.hackathon.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final String issuer;

    public JwtService(
        @Value("${JWT_SECRET:Use_the_secret_from_env}") String secret,
        @Value("${JWT_ISSUER:carbontrack-api}") String issuer
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
    }

    public String generateToken(String email, String role, long expirationMillis) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationMillis);

        return Jwts.builder()
            .issuer(issuer)
            .subject(email)
            .claim("email", email)
            .claim("role", role)
            .claim("type", "access")
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(signingKey)
            .compact();
    }

    public String generateRefreshToken(String email, long expirationMillis) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationMillis);

        return Jwts.builder()
            .issuer(issuer)
            .subject(email)
            .claim("email", email)
            .claim("type", "refresh")
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(signingKey)
            .compact();
    }

    public String extractEmailFromRefreshToken(String refreshToken) {
        Claims claims = Jwts.parser()
            .verifyWith(signingKey)
            .requireIssuer(issuer)
            .build()
            .parseSignedClaims(refreshToken)
            .getPayload();

        String tokenType = claims.get("type", String.class);
        if (!"refresh".equals(tokenType)) {
            throw new JwtException("Token is not a refresh token");
        }

        return claims.getSubject();
    }
}
