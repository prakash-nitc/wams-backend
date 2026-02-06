package com.nit.arwms.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

/**
 * Service layer for Workflow business logic.
 * 
 * This class handles all workflow-related business operations,
 * keeping the controller focused on HTTP concerns only.
 * 
 * Key Concept: @Service Annotation
 * --------------------------------
 * - Marks this class as a Spring-managed service component
 * - Spring automatically creates a single instance (singleton by default)
 * - Can be injected into other components (like controllers)
 * - Semantically indicates "this is where business logic lives"
 */
@Service
public class WorkflowService {
    
    // In-memory storage for workflows (will be replaced by database in Phase 4)
    // These are data concerns, not HTTP concerns - they belong in the service layer
    private final List<Workflow> workflows = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Retrieves all workflows in the system.
     * 
     * @return List of all workflows
     */
    public List<Workflow> getAllWorkflows() {
        return workflows;
    }
}
