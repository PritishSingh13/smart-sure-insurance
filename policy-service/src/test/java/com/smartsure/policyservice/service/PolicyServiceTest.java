package com.smartsure.policyservice.service;

import com.smartsure.policyservice.model.Policy;
import com.smartsure.policyservice.model.PolicyPurchase;
import com.smartsure.policyservice.repository.PolicyPurchaseRepository;
import com.smartsure.policyservice.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PolicyPurchaseRepository purchaseRepository;

    @InjectMocks
    private PolicyService policyService;

    private Policy policy;

    @BeforeEach
    void setUp() {
        policy = new Policy();
        policy.setId(1L);
        policy.setPolicyName("Health Premium");
        policy.setPolicyType("HEALTH");
        policy.setPremium(5000.0);
        policy.setDuration(12);
    }

    @Test
    void testCreatePolicy_Single() {
        Map<String, Object> request = new HashMap<>();
        request.put("policyName", "Health Premium");
        request.put("policyType", "HEALTH");

        when(policyRepository.save(any(Policy.class))).thenReturn(policy);

        Object result = policyService.createPolicy(request);

        assertTrue(result instanceof Policy);
        Policy savedPolicy = (Policy) result;
        assertEquals("Health Premium", savedPolicy.getPolicyName());
        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    @Test
    void testCreatePolicy_Multiple() {
        Map<String, Object> request1 = new HashMap<>();
        request1.put("policyName", "Health Premium");
        Map<String, Object> request2 = new HashMap<>();
        request2.put("policyName", "Auto Basic");

        List<Object> requestList = Arrays.asList(request1, request2);

        when(policyRepository.save(any(Policy.class))).thenReturn(policy);

        Object result = policyService.createPolicy(requestList);

        assertTrue(result instanceof List);
        List<?> savedPolicies = (List<?>) result;
        assertEquals(2, savedPolicies.size());
        verify(policyRepository, times(2)).save(any(Policy.class));
    }

    @Test
    void testCreatePolicy_NullBody() {
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> policyService.createPolicy(null));
        assertEquals("Request body cannot be empty", exception.getMessage());
    }

    @Test
    void testUpdatePolicy_Success() {
        Policy updatedPolicy = new Policy();
        updatedPolicy.setPolicyName("Health Basic");
        updatedPolicy.setPolicyType("HEALTH");
        updatedPolicy.setPremium(2000.0);
        updatedPolicy.setDuration(6);

        when(policyRepository.findById(anyLong())).thenReturn(Optional.of(policy));
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);

        Policy result = policyService.updatePolicy(1L, updatedPolicy);

        assertEquals("Health Basic", result.getPolicyName());
        assertEquals(2000.0, result.getPremium());
        assertEquals(6, result.getDuration());
        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    @Test
    void testUpdatePolicy_NotFound() {
        when(policyRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> policyService.updatePolicy(1L, new Policy()));

        assertEquals("Policy not found with id: 1", exception.getMessage());
    }

    @Test
    void testGetAllPolicies() {
        when(policyRepository.findAll()).thenReturn(Arrays.asList(policy));

        List<Policy> result = policyService.getAllPolicies();

        assertEquals(1, result.size());
        assertEquals("Health Premium", result.get(0).getPolicyName());
    }

    @Test
    void testGetPolicyById_Success() {
        when(policyRepository.findById(anyLong())).thenReturn(Optional.of(policy));

        Policy result = policyService.getPolicyById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testPurchasePolicy_Success() {
        when(policyRepository.findById(anyLong())).thenReturn(Optional.of(policy));
        when(purchaseRepository.save(any(PolicyPurchase.class))).thenReturn(new PolicyPurchase());

        String result = policyService.purchasePolicy(1L, "user@example.com");

        assertTrue(result.contains("Policy Purchased Successfully!"));
        assertTrue(result.contains("user@example.com"));
        verify(purchaseRepository, times(1)).save(any(PolicyPurchase.class));
    }

    @Test
    void testPurchasePolicy_InvalidInput() {
        RuntimeException exception1 = assertThrows(RuntimeException.class, 
                () -> policyService.purchasePolicy(null, "user@example.com"));
        assertEquals("Policy ID is required", exception1.getMessage());

        RuntimeException exception2 = assertThrows(RuntimeException.class, 
                () -> policyService.purchasePolicy(1L, null));
        assertEquals("User email is required", exception2.getMessage());
    }

    @Test
    void testDeletePolicy() {
        String result = policyService.deletePolicy(1L);

        assertEquals("Policy deleted successfully", result);
        verify(policyRepository, times(1)).deleteById(1L);
    }
}
