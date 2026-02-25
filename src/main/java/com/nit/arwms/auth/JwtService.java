package com.nit.arwms.auth;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Service for JWT token generation and validation.
 *
 * Key Concept: JWT (JSON Web Token)
 * -----------------------------------
 * A JWT is a self-contained token with three parts:
 * Header.Payload.Signature
 *
 * Header: Algorithm + type (e.g., HS256, JWT)
 * Payload: Claims — data like username, role, expiry
 * Signature: HMAC-SHA256(header + payload, SECRET_KEY)
 *
 * The server never stores JWT tokens — it validates them by
 * recalculating the signature using the secret key.
 *
 * Flow:
 * 1. User logs in → server creates JWT with username + role
 * 2. Client stores JWT and sends it in Authorization header
 * 3. Server validates JWT on every request (no DB lookup needed)
 */
@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        // Decode Base64-encoded secret into a signing key
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a JWT token for the given user.
     * The token contains: username (subject), role (claim), issue time, expiry.
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts the username from a JWT token.
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extracts the role from a JWT token.
     */
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    /**
     * Validates the token: checks signature and expiry.
     */
    public boolean isTokenValid(String token, String username) {
        String tokenUsername = extractUsername(token);
        return tokenUsername.equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
