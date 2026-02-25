package com.nit.arwms.workflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

import com.nit.arwms.exception.InvalidTransitionException;
import com.nit.arwms.exception.WorkflowNotFoundException;

/**
 * Unit tests for WorkflowController.
 *
 * Phase 7 Update: Added tests for state transitions (PATCH endpoint),
 * including valid transitions, invalid transitions (409), and role
 * authorization failures.
 */
@WebMvcTest(WorkflowController.class)
class WorkflowControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private WorkflowService workflowService;

        // ─── GET /api/workflows ─────────────────────────────────────────

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
                                .andExpect(jsonPath("$[0].status").value("DRAFT"));
        }

        // ─── POST /api/workflows ────────────────────────────────────────

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
                                .andExpect(jsonPath("$.status").value("DRAFT"));
        }

        @Test
        void createWorkflow_returnsBadRequestWithErrorBody() throws Exception {
                mockMvc.perform(post("/api/workflows")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"title\": \"\"}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.error").value("Bad Request"));
        }

        // ─── GET /api/workflows/{id} ────────────────────────────────────

        @Test
        void getWorkflowById_returnsWorkflowWhenFound() throws Exception {
                WorkflowResponse workflow = new WorkflowResponse(1L, "Leave Request",
                                "Employee leave approval", "DRAFT", LocalDateTime.now());
                when(workflowService.findById(1L)).thenReturn(workflow);

                mockMvc.perform(get("/api/workflows/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        void getWorkflowById_returnsNotFoundWithErrorBody() throws Exception {
                when(workflowService.findById(999L))
                                .thenThrow(new WorkflowNotFoundException(999L));

                mockMvc.perform(get("/api/workflows/999"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.message").value("Workflow not found with id: 999"));
        }

        // ─── PATCH /api/workflows/{id}/transition ───────────────────────

        @Test
        void transitionWorkflow_submitsSuccessfully() throws Exception {
                WorkflowResponse response = new WorkflowResponse(1L, "Leave Request",
                                "desc", "SUBMITTED", LocalDateTime.now());
                when(workflowService.transitionWorkflow(eq(1L), any(WorkflowTransitionRequest.class)))
                                .thenReturn(response);

                mockMvc.perform(patch("/api/workflows/1/transition")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"targetStatus\": \"SUBMITTED\", \"role\": \"REQUESTER\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("SUBMITTED"));
        }

        @Test
        void transitionWorkflow_approvesSuccessfully() throws Exception {
                WorkflowResponse response = new WorkflowResponse(1L, "Leave Request",
                                "desc", "APPROVED", LocalDateTime.now());
                when(workflowService.transitionWorkflow(eq(1L), any(WorkflowTransitionRequest.class)))
                                .thenReturn(response);

                mockMvc.perform(patch("/api/workflows/1/transition")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"targetStatus\": \"APPROVED\", \"role\": \"APPROVER\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("APPROVED"));
        }

        @Test
        void transitionWorkflow_returns409ForInvalidTransition() throws Exception {
                when(workflowService.transitionWorkflow(eq(1L), any(WorkflowTransitionRequest.class)))
                                .thenThrow(new InvalidTransitionException(
                                                "Cannot transition from DRAFT to APPROVED"));

                mockMvc.perform(patch("/api/workflows/1/transition")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"targetStatus\": \"APPROVED\", \"role\": \"APPROVER\"}"))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.status").value(409))
                                .andExpect(jsonPath("$.error").value("Conflict"))
                                .andExpect(jsonPath("$.message").value("Cannot transition from DRAFT to APPROVED"));
        }

        @Test
        void transitionWorkflow_returns409ForUnauthorizedRole() throws Exception {
                when(workflowService.transitionWorkflow(eq(1L), any(WorkflowTransitionRequest.class)))
                                .thenThrow(new InvalidTransitionException(
                                                "Role 'REQUESTER' is not authorized for this transition. Required role: APPROVER"));

                mockMvc.perform(patch("/api/workflows/1/transition")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"targetStatus\": \"APPROVED\", \"role\": \"REQUESTER\"}"))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.status").value(409));
        }

        @Test
        void transitionWorkflow_returns404ForNonExistentWorkflow() throws Exception {
                when(workflowService.transitionWorkflow(eq(999L), any(WorkflowTransitionRequest.class)))
                                .thenThrow(new WorkflowNotFoundException(999L));

                mockMvc.perform(patch("/api/workflows/999/transition")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"targetStatus\": \"SUBMITTED\", \"role\": \"REQUESTER\"}"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404));
        }
}
