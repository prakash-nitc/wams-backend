package com.nit.arwms.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for User entity.
 * Spring Data JPA auto-implements this interface.
 *
 * findByUsername is a derived query method — Spring generates the SQL
 * based on the method name: SELECT * FROM users WHERE username = ?
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
