package com.nit.arwms.workflow;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Workflow entity.
 * 
 * Key Concept: Spring Data JPA Repository (Phase 4)
 * --------------------------------------------------
 * By extending JpaRepository, Spring automatically provides:
 * 
 * - findAll() → SELECT * FROM workflows
 * - findById(id) → SELECT * FROM workflows WHERE id = ?
 * - save(entity) → INSERT or UPDATE
 * - deleteById(id) → DELETE FROM workflows WHERE id = ?
 * - count() → SELECT COUNT(*) FROM workflows
 * - existsById(id) → Check if record exists
 * 
 * No implementation code needed! Spring generates it at runtime.
 * 
 * JpaRepository<Workflow, Long>:
 * - Workflow = the entity type
 * - Long = the type of the primary key (@Id field)
 */
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    // No methods needed! JpaRepository provides all basic CRUD operations.
    // Custom query methods can be added here later if needed.
}
