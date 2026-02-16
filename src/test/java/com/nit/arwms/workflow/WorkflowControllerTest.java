package com.nit.arwms.workflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nit.arwms.exception.WorkflowNotFoundException;

/**
 * Unit tests for WorkflowController.
 *
 * Phase 6 Update: Tests verify standardized error responses
 * from GlobalExceptionHandler for 404 and 400 cases.
 */
@WebMvcTest(WorkflowController.class)
class WorkflowControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private WorkflowService workflowService;

        @Test
        void getAllWorkflows_returnsEmptyListInitially() throws Exception {
                when(workflowService.getAllWorkflows()).thenReturn(Collections.emptyList());

                mockMvc.perform(get("/api/workflows"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        void getAllWorkflows_returnsWorkflowList() throws Exception {
                WorkflowResponse wf = new WorkflowResponse(1L, "Leave Request",
                                "Employee leave approval", "DRAFT", LocalDateTime.now());
                when(workflowService.getAllWorkflows()).thenReturn(List.of(wf));

                mockMvc.perform(get("/api/workflows"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].title").value("Leave Request"))
                                .andExpect(jsonPath("$[0].status").value("DRAFT"));
        }

        @Test
        void createWorkflow_returnsCreatedWorkflow() throws Exception {
                WorkflowResponse response = new WorkflowResponse(1L, "Leave Request",
                                "Employee leave approval workflow", "DRAFT", LocalDateTime.now());
                when(workflowService.createWorkflow(any(WorkflowRequest.class))).thenReturn(response);

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
                                .andExpect(jsonPath("$.status").value("DRAFT"))
                                .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        void createWorkflow_returnsBadRequestWithErrorBody() throws Exception {
                String requestBody = """
                                {
                                    "title": "",
                                    "description": "Some description"
                                }
                                """;

                mockMvc.perform(post("/api/workflows")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.error").value("Bad Request"))
                                .andExpect(jsonPath("$.message").exists())
                                .andExpect(jsonPath("$.path").value("/api/workflows"));
        }

        @Test
        void createWorkflow_returnsBadRequestWhenTitleIsMissing() throws Exception {
                String requestBody = """
                                {
                                    "description": "Some description"
                                }
                                """;

                mockMvc.perform(post("/api/workflows")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        void getWorkflowById_returnsWorkflowWhenFound() throws Exception {
                WorkflowResponse workflow = new WorkflowResponse(1L, "Leave Request",
                                "Employee leave approval", "DRAFT", LocalDateTime.now());
                when(workflowService.findById(1L)).thenReturn(workflow);

                mockMvc.perform(get("/api/workflows/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.title").value("Leave Request"));
        }

        @Test
        void getWorkflowById_returnsNotFoundWithErrorBody() throws Exception {
                when(workflowService.findById(999L))
                                .thenThrow(new WorkflowNotFoundException(999L));

                mockMvc.perform(get("/api/workflows/999"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.error").value("Not Found"))
                                .andExpect(jsonPath("$.message").value("Workflow not found with id: 999"))
                                .andExpect(jsonPath("$.path").value("/api/workflows/999"));
        }
}
