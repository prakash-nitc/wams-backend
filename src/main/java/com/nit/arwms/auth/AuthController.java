package com.nit.arwms.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * Authentication controller for register and login.
 *
 * Key Concept: Authentication vs Authorization
 * -----------------------------------------------
 * - Authentication: "Who are you?" (login, verify credentials)
 * - Authorization: "What can you do?" (role-based access control)
 *
 * POST /api/auth/register — Authentication (create identity)
 * POST /api/auth/login — Authentication (verify identity, get token)
 * Token on every request — Authorization (check if allowed)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * POST /api/auth/register - Register a new user
     *
     * Password is hashed with BCrypt before storing.
     * Returns a JWT token so user is immediately authenticated.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.role());
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        AuthResponse response = new AuthResponse(token, user.getUsername(), user.getRole().name());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/login - Authenticate and get JWT token
     *
     * Verifies password against BCrypt hash, returns JWT if valid.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(user);
        AuthResponse response = new AuthResponse(token, user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(response);
    }
}
