package com.nit.arwms.workflow;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a workflow in the system.
 * A workflow is a multi-step process that requires approvals.
 * 
 * Key Concept: JPA Entity (Phase 4)
 * ----------------------------------
 * 
 * @Entity — Marks this class as a JPA entity (maps to a database table)
 * @Table — Specifies the table name (optional, defaults to class name)
 * @Id — Marks the primary key field
 * @GeneratedValue — Database auto-generates the ID
 * @Column — Customizes column mapping (optional for simple cases)
 * 
 *         Hibernate will auto-create a "workflows" table with columns matching
 *         these fields.
 */
@Entity
@Table(name = "workflows")
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Default constructor (required by JPA)
    public Workflow() {
    }

    // Constructor with all fields
    public Workflow(Long id, String title, String description, String status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
