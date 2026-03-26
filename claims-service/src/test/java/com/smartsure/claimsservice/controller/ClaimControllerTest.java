package com.smartsure.claimsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartsure.claimsservice.dto.ClaimDto;
import com.smartsure.claimsservice.entity.Claim;
import com.smartsure.claimsservice.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
public class ClaimControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClaimService claimService;

    @InjectMocks
    private ClaimController claimController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(claimController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testUploadClaimWithFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "dummy content".getBytes());

        ClaimDto expectedDto = new ClaimDto();
        expectedDto.setClaimNumber("CLM-123");
        expectedDto.setStatus("UPLOADED");

        when(claimService.uploadClaimWithFile(any(), anyLong(), anyString(), anyString())).thenReturn(expectedDto);

        mockMvc.perform(multipart("/api/claims/upload")
                .file(file)
                .param("policyId", "101")
                .param("claimantName", "John Doe")
                .header("X-Auth-User", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.claimNumber").value("CLM-123"))
                .andExpect(jsonPath("$.status").value("UPLOADED"));
    }

    @Test
    void testInitiateClaim() throws Exception {
        ClaimDto expectedDto = new ClaimDto();
        expectedDto.setClaimNumber("CLM-123");
        expectedDto.setStatus("SUBMITTED");

        when(claimService.initiateClaim(anyString(), anyString())).thenReturn(expectedDto);

        mockMvc.perform(post("/api/claims/initiate")
                .param("claimNumber", "CLM-123")
                .header("X-Auth-User", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    void testGetStatus() throws Exception {
        ClaimDto expectedDto = new ClaimDto();
        expectedDto.setClaimNumber("CLM-123");
        expectedDto.setStatus("UPLOADED");

        when(claimService.getClaimStatus(anyString())).thenReturn(expectedDto);

        mockMvc.perform(get("/api/claims/status/CLM-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UPLOADED"));
    }

    @Test
    void testReviewClaim() throws Exception {
        when(claimService.reviewClaim(anyLong(), anyString())).thenReturn("Claim APPROVED");

        mockMvc.perform(put("/api/admin/claims/1/review")
                .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(content().string("Claim APPROVED"));
    }

    @Test
    void testGetAllClaims() throws Exception {
        Claim claim1 = new Claim();
        claim1.setClaimNumber("CLM-123");
        Claim claim2 = new Claim();
        claim2.setClaimNumber("CLM-124");

        when(claimService.getAllClaims()).thenReturn(Arrays.asList(claim1, claim2));

        mockMvc.perform(get("/api/admin/claims"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].claimNumber").value("CLM-123"))
                .andExpect(jsonPath("$[1].claimNumber").value("CLM-124"));
    }

    @Test
    void testGetReports() throws Exception {
        Map<String, Long> report = new HashMap<>();
        report.put("TOTAL_CLAIMS", 5L);

        when(claimService.getReportData()).thenReturn(report);

        mockMvc.perform(get("/api/admin/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.TOTAL_CLAIMS").value(5));
    }
}
