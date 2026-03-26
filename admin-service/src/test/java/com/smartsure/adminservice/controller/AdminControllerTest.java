package com.smartsure.adminservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartsure.adminservice.client.ClaimsClient;
import com.smartsure.adminservice.client.PolicyClient;
import com.smartsure.adminservice.dto.ClaimDto;
import com.smartsure.adminservice.dto.PolicyDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClaimsClient claimsClient;

    @Mock
    private PolicyClient policyClient;

    @InjectMocks
    private AdminController adminController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testReviewClaim() throws Exception {
        when(claimsClient.reviewClaim(anyLong(), anyString())).thenReturn("Claim APPROVED");

        mockMvc.perform(put("/api/admin/claims/1/review")
                .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(content().string("Claim APPROVED"));
    }

    @Test
    void testGetAllClaims() throws Exception {
        ClaimDto claim1 = new ClaimDto();
        claim1.setId(1L);
        ClaimDto claim2 = new ClaimDto();
        claim2.setId(2L);

        when(claimsClient.getAllClaims()).thenReturn(Arrays.asList(claim1, claim2));

        mockMvc.perform(get("/api/admin/claims"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void testGetReports() throws Exception {
        Map<String, Long> report = new HashMap<>();
        report.put("TOTAL", 10L);

        when(claimsClient.getReports()).thenReturn(report);

        mockMvc.perform(get("/api/admin/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.TOTAL").value(10));
    }

    @Test
    void testCreatePolicy() throws Exception {
        PolicyDto policy = new PolicyDto();
        policy.setPolicyName("Gold Policy");

        when(policyClient.createPolicy(any(PolicyDto.class))).thenReturn(policy);

        mockMvc.perform(post("/api/admin/policies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(policy)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyName").value("Gold Policy"));
    }

    @Test
    void testUpdatePolicy() throws Exception {
        PolicyDto policy = new PolicyDto();
        policy.setPolicyName("Updated Gold Policy");

        when(policyClient.updatePolicy(anyLong(), any(PolicyDto.class))).thenReturn(policy);

        mockMvc.perform(put("/api/admin/policies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(policy)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyName").value("Updated Gold Policy"));
    }

    @Test
    void testDeletePolicy() throws Exception {
        when(policyClient.deletePolicy(anyLong())).thenReturn("Policy deleted");

        mockMvc.perform(delete("/api/admin/policies/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Policy deleted"));
    }
}
