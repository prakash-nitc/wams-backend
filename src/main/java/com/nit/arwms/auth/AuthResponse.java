package com.nit.arwms.auth;

/**
 * DTO for authentication responses (login/register).
 * Contains the JWT token and user info.
 */
public record AuthResponse(
        String token,
        String username,
        String role) {
}
