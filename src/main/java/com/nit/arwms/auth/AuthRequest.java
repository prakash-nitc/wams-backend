package com.nit.arwms.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for login requests.
 */
public record AuthRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Password is required") String password) {
}
