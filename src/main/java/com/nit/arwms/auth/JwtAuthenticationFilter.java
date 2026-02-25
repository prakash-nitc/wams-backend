package com.nit.arwms.auth;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT Authentication Filter — runs on EVERY request.
 *
 * Key Concept: Filter Chain
 * ---------------------------
 * Spring Security processes requests through a chain of filters.
 * This filter runs BEFORE the controller and:
 *
 * 1. Extracts the JWT from the Authorization header
 * 2. Validates the token (signature + expiry)
 * 3. Loads the user and sets it in the SecurityContext
 *
 * If the token is missing or invalid, the request continues
 * without authentication (and will be rejected by SecurityConfig
 * if the endpoint requires auth).
 *
 * Authorization Header Format:
 * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Extract the Authorization header
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No JWT token — continue without authentication
            filterChain.doFilter(request, response);
            return;
        }

        // Step 2: Extract the token (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        try {
            // Step 3: Extract username from token
            String username = jwtService.extractUsername(token);

            // Step 4: If not already authenticated, validate and set auth
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByUsername(username).orElse(null);

                if (user != null && jwtService.isTokenValid(token, username)) {
                    // Step 5: Create authentication token and set in context
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Invalid token — continue without authentication
            // SecurityConfig will reject if endpoint requires auth
        }

        filterChain.doFilter(request, response);
    }
}
