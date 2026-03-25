package com.smartsure.policyservice.service;

import com.smartsure.policyservice.model.Policy;
import com.smartsure.policyservice.model.PolicyPurchase;
import com.smartsure.policyservice.repository.PolicyPurchaseRepository;
import com.smartsure.policyservice.repository.PolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @InjectMocks
    private PolicyService policyService;

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PolicyPurchaseRepository purchaseRepository;

    // =========================
    // TEST 1: CREATE POLICY
    // =========================
    @Test
    void testCreatePolicy() {

        Policy policy = new Policy();
        policy.setId(1L);
        policy.setPolicyName("Health");

        when(policyRepository.save(any(Policy.class)))
                .thenReturn(policy);

        Policy result = policyService.createPolicy(policy);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Health", result.getPolicyName());
    }

    // =========================
    // TEST 2: UPDATE POLICY
    // =========================
    @Test
    void testUpdatePolicy() {

        Policy existing = new Policy(1L, "Old", "Life", 1000.0, 12);

        Policy updated = new Policy(1L, "New", "Health", 2000.0, 24);

        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(policyRepository.save(any(Policy.class)))
                .thenReturn(updated);

        Policy result = policyService.updatePolicy(1L, updated);

        assertEquals("New", result.getPolicyName());
        assertEquals("Health", result.getPolicyType());
    }

    // =========================
    // TEST 3: GET ALL POLICIES
    // =========================
    @Test
    void testGetAllPolicies() {

        List<Policy> list = Arrays.asList(
                new Policy(1L, "A", "Type1", 1000.0, 12),
                new Policy(2L, "B", "Type2", 2000.0, 24)
        );

        when(policyRepository.findAll()).thenReturn(list);

        List<Policy> result = policyService.getAllPolicies();

        assertEquals(2, result.size());
    }

    // =========================
    // TEST 4: GET POLICY BY ID
    // =========================
    @Test
    void testGetPolicyById() {

        Policy policy = new Policy(1L, "A", "Type1", 1000.0, 12);

        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(policy));

        Policy result = policyService.getPolicyById(1L);

        assertEquals(1L, result.getId());
    }

    // =========================
    // TEST 5: PURCHASE POLICY
    // =========================
    @Test
    void testPurchasePolicy() {

        Policy policy = new Policy(1L, "A", "Type1", 1000.0, 12);

        when(policyRepository.findById(1L))
                .thenReturn(Optional.of(policy));

        when(purchaseRepository.save(any(PolicyPurchase.class)))
                .thenReturn(new PolicyPurchase());

        String result = policyService.purchasePolicy(1L, "test@gmail.com");

        assertEquals("Policy purchased successfully", result);
    }

    // =========================
    // TEST 6: DELETE POLICY
    // =========================
    @Test
    void testDeletePolicy() {

        doNothing().when(policyRepository).deleteById(1L);

        String result = policyService.deletePolicy(1L);

        assertEquals("Policy deleted successfully", result);

        verify(policyRepository, times(1)).deleteById(1L);
    }
}