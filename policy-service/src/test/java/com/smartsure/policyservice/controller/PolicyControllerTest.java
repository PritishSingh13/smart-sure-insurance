package com.smartsure.policyservice.controller;

import com.smartsure.policyservice.model.Policy;
import com.smartsure.policyservice.service.PolicyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyService policyService;

    // =========================
    // TEST 1: GET ALL POLICIES
    // =========================
    @Test
    void testGetAllPolicies() throws Exception {

        Policy p1 = new Policy(1L, "Health", "Medical", 1000.0, 12);
        Policy p2 = new Policy(2L, "Life", "Insurance", 2000.0, 24);

        when(policyService.getAllPolicies())
                .thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/policies"))
                .andExpect(status().isOk());
    }

    // =========================
    // TEST 2: GET POLICY BY ID
    // =========================
    @Test
    void testGetPolicyById() throws Exception {

        Policy policy = new Policy(1L, "Health", "Medical", 1000.0, 12);

        when(policyService.getPolicyById(1L))
                .thenReturn(policy);

        mockMvc.perform(get("/api/policies/1"))
                .andExpect(status().isOk());
    }

    // =========================
    // TEST 3: CREATE POLICY (ADMIN)
    // =========================
    @Test
    void testCreatePolicy() throws Exception {

        Policy policy = new Policy(1L, "Health", "Medical", 1000.0, 12);

        when(policyService.createPolicy(any(Policy.class)))
                .thenReturn(policy);

        mockMvc.perform(post("/api/admin/policies")
                        .header("X-User-Role", "ADMIN")
                        .contentType("application/json")
                        .content("""
                                {
                                    "policyName": "Health",
                                    "policyType": "Medical",
                                    "premium": 1000,
                                    "duration": 12
                                }
                                """))
                .andExpect(status().isOk());
    }

    // =========================
    // TEST 4: UPDATE POLICY (ADMIN)
    // =========================
    @Test
    void testUpdatePolicy() throws Exception {

        Policy updated = new Policy(1L, "Updated", "Life", 1500.0, 18);

        when(policyService.updatePolicy(eq(1L), any(Policy.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/admin/policies/1")
                        .header("X-User-Role", "ADMIN")
                        .contentType("application/json")
                        .content("""
                                {
                                    "policyName": "Updated",
                                    "policyType": "Life",
                                    "premium": 1500,
                                    "duration": 18
                                }
                                """))
                .andExpect(status().isOk());
    }

    // =========================
    // TEST 5: DELETE POLICY (ADMIN)
    // =========================
    @Test
    void testDeletePolicy() throws Exception {

        when(policyService.deletePolicy(1L))
                .thenReturn("Policy deleted successfully");

        mockMvc.perform(delete("/api/admin/policies/1")
                        .header("X-User-Role", "ADMIN"))
                .andExpect(status().isOk());
    }

    // =========================
    // TEST 6: PURCHASE POLICY (CUSTOMER)
    // =========================
    @Test
    void testPurchasePolicy() throws Exception {

        when(policyService.purchasePolicy(1L, "user@gmail.com"))
                .thenReturn("Policy purchased successfully");

        mockMvc.perform(post("/api/policies/purchase")
                        .header("X-User-Role", "CUSTOMER")
                        .header("X-User-Email", "user@gmail.com")
                        .contentType("application/json")
                        .content("""
                                {
                                    "policyId": 1
                                }
                                """))
                .andExpect(status().isOk());
    }
}