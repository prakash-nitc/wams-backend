package com.nit.arwms.workflow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WorkflowController.class)
class WorkflowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllWorkflows_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(get("/api/workflows"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void createWorkflow_returnsCreatedWorkflow() throws Exception {
        String requestBody = """
            {
                "title": "Leave Request",
                "description": "Employee leave approval workflow"
            }
            """;

        mockMvc.perform(post("/api/workflows")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Leave Request"))
            .andExpect(jsonPath("$.description").value("Employee leave approval workflow"))
            .andExpect(jsonPath("$.status").value("DRAFT"))
            .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void getWorkflowById_returnsNotFoundForNonExistentId() throws Exception {
        mockMvc.perform(get("/api/workflows/999"))
            .andExpect(status().isNotFound());
    }
}
