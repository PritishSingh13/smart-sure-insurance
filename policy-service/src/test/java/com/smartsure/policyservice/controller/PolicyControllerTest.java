package com.smartsure.policyservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartsure.policyservice.dto.PurchaseRequest;
import com.smartsure.policyservice.model.Policy;
import com.smartsure.policyservice.service.PolicyService;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PolicyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PolicyService policyService;

    @InjectMocks
    private PolicyController policyController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(policyController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreatePolicy_AdminRole() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("policyName", "Super Life");

        Policy expectedPolicy = new Policy();
        expectedPolicy.setPolicyName("Super Life");

        when(policyService.createPolicy(any())).thenReturn(expectedPolicy);

        mockMvc.perform(post("/api/admin/policies")
                .header("X-User-Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyName").value("Super Life"));
    }

    @Test
    void testCreatePolicy_NonAdminRole_ThrowsException() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("policyName", "Super Life");

        jakarta.servlet.ServletException exception = org.junit.jupiter.api.Assertions.assertThrows(
                jakarta.servlet.ServletException.class,
                () -> mockMvc.perform(post("/api/admin/policies")
                        .header("X-User-Role", "CUSTOMER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
        );

        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(exception.getCause().getMessage().contains("ADMIN only"));
    }

    @Test
    void testUpdatePolicy_AdminRole() throws Exception {
        Policy policy = new Policy();
        policy.setPolicyName("Updated Life");
        policy.setPolicyType("HEALTH");
        policy.setPremium(100.0);
        policy.setDuration(12);

        when(policyService.updatePolicy(anyLong(), any(Policy.class))).thenReturn(policy);

        mockMvc.perform(put("/api/admin/policies/1")
                .header("X-User-Role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(policy)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyName").value("Updated Life"));
    }

    @Test
    void testDeletePolicy_AdminRole() throws Exception {
        when(policyService.deletePolicy(anyLong())).thenReturn("Policy deleted successfully");

        mockMvc.perform(delete("/api/admin/policies/1")
                .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(content().string("Policy deleted successfully"));
    }

    @Test
    void testGetAllPolicies() throws Exception {
        Policy policy1 = new Policy();
        policy1.setPolicyName("Policy 1");
        Policy policy2 = new Policy();
        policy2.setPolicyName("Policy 2");

        when(policyService.getAllPolicies()).thenReturn(Arrays.asList(policy1, policy2));

        mockMvc.perform(get("/api/policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].policyName").value("Policy 1"));
    }

    @Test
    void testGetPolicyById() throws Exception {
        Policy policy = new Policy();
        policy.setId(1L);
        policy.setPolicyName("Policy 1");

        when(policyService.getPolicyById(1L)).thenReturn(policy);

        mockMvc.perform(get("/api/policies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyName").value("Policy 1"));
    }

    @Test
    void testPurchasePolicy_CustomerRole() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setPolicyId(1L);

        when(policyService.purchasePolicy(anyLong(), anyString())).thenReturn("Policy Purchased Successfully!");

        mockMvc.perform(post("/api/policies/purchase")
                .header("X-User-Email", "john@example.com")
                .header("X-User-Role", "CUSTOMER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Policy Purchased Successfully!"));
    }

    @Test
    void testPurchasePolicy_WrongRole_ThrowsException() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setPolicyId(1L);

        jakarta.servlet.ServletException exception = org.junit.jupiter.api.Assertions.assertThrows(
                jakarta.servlet.ServletException.class,
                () -> mockMvc.perform(post("/api/policies/purchase")
                        .header("X-User-Email", "john@example.com")
                        .header("X-User-Role", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
        );

        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(exception.getCause().getMessage().contains("CUSTOMER only"));
    }
}
