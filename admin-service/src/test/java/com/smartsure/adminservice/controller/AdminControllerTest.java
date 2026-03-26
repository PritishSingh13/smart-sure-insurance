package com.smartsure.adminservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartsure.adminservice.client.ClaimsClient;
import com.smartsure.adminservice.client.PolicyClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClaimsClient claimsClient;

    @MockBean
    private PolicyClient policyClient;

    // =========================
    // REVIEW CLAIM
    // =========================
    @Test
    void testReviewClaim() throws Exception {

        when(claimsClient.reviewClaim(1L, "APPROVED"))
                .thenReturn("Claim Reviewed");

        mockMvc.perform(put("/api/admin/claims/1/review")
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(content().string("Claim Reviewed"));
    }

    // =========================
    // GET ALL CLAIMS
    // =========================
    @Test
    void testGetAllClaims() throws Exception {

        when(claimsClient.getAllClaims())
                .thenReturn(List.of(new HashMap<>()));

        mockMvc.perform(get("/api/admin/claims"))
                .andExpect(status().isOk());
    }

    // =========================
    // REPORTS
    // =========================
    @Test
    void testReports() throws Exception {

        Map<String, Long> response = new HashMap<>();
        response.put("TOTAL", 5L);

        when(claimsClient.getReports())
                .thenReturn(response);

        mockMvc.perform(get("/api/admin/reports"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    // =========================
    // CREATE POLICY
    // =========================
    @Test
    void testCreatePolicy() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("name", "Health");

        Map<String, Object> response = new HashMap<>();
        response.put("id", 1);
        response.put("name", "Health");

        when(policyClient.createPolicy(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/admin/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // =========================
    // UPDATE POLICY
    // =========================
    @Test
    void testUpdatePolicy() throws Exception {

        Map<String, Object> request = new HashMap<>();
        request.put("name", "Updated");

        Map<String, Object> response = new HashMap<>();
        response.put("id", 1);
        response.put("name", "Updated");

        when(policyClient.updatePolicy(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/admin/policies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // =========================
    // DELETE POLICY
    // =========================
    @Test
    void testDeletePolicy() throws Exception {

        when(policyClient.deletePolicy(1L))
                .thenReturn("Deleted");

        mockMvc.perform(delete("/api/admin/policies/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted"));
    }
}