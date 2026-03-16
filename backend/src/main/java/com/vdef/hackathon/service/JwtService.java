package com.vdef.hackathon.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final String issuer;

    public JwtService(
        @Value("${JWT_SECRET:Use_the_secret_from_env}") String secret,
        @Value("${JWT_ISSUER:carbontrack-api}") String issuer
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = issuer;
    }

    public String generateToken(String email, long expirationMillis) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationMillis);

        return JWT.create()
            .withIssuer(issuer)
            .withSubject(email)
            .withClaim("email", email)
            .withClaim("type", "access")
            .withIssuedAt(now)
            .withExpiresAt(expiresAt)
            .sign(algorithm);
    }

    public String generateRefreshToken(String email, long expirationMillis) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationMillis);

        return JWT.create()
            .withIssuer(issuer)
            .withSubject(email)
            .withClaim("email", email)
            .withClaim("type", "refresh")
            .withIssuedAt(now)
            .withExpiresAt(expiresAt)
            .sign(algorithm);
    }

    public String extractEmailFromRefreshToken(String refreshToken) {
        DecodedJWT decoded = JWT.require(algorithm)
            .withIssuer(issuer)
            .build()
            .verify(refreshToken);

        String tokenType = decoded.getClaim("type").asString();
        if (!"refresh".equals(tokenType)) {
            throw new JWTVerificationException("Token is not a refresh token");
        }

        return decoded.getSubject();
    }
}
