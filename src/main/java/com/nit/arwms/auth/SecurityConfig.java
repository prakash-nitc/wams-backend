package com.nit.arwms.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration.
 *
 * Key Concept: Security Filter Chain
 * -------------------------------------
 * Defines which endpoints are public and which require authentication/roles.
 *
 * - /api/auth/** → Public (register, login)
 * - /api/health → Public (health check)
 * - /h2-console → Public (development database browser)
 * - Everything else → Requires JWT authentication
 *
 * Key Concept: Stateless Sessions
 * ---------------------------------
 * We disable sessions because JWT is stateless — the token carries
 * all the info needed. No server-side session storage needed.
 *
 * Key Concept: BCrypt Password Encoder
 * ---------------------------------------
 * Passwords are never stored in plain text. BCrypt hashes them with
 * a random salt, making rainbow table attacks useless.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (not needed for stateless JWT APIs)
                .csrf(csrf -> csrf.disable())

                // Allow H2 console to load in iframes
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // Stateless sessions (JWT-based, no cookies)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Endpoint authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Workflow endpoints — authenticated users only
                        .requestMatchers(HttpMethod.GET, "/api/workflows/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/workflows").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/workflows/*/transition").authenticated()

                        // Everything else requires authentication
                        .anyRequest().authenticated())

                // Add JWT filter BEFORE the default username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt password encoder bean.
     * Used by AuthController to hash passwords during registration
     * and verify them during login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
