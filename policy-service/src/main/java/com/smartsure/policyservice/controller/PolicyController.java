package com.smartsure.policyservice.controller;

import com.smartsure.policyservice.dto.PurchaseRequest;
import com.smartsure.policyservice.model.Policy;
import com.smartsure.policyservice.service.PolicyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    // ================= ADMIN =================

    @PostMapping("/api/admin/policies")
    public Policy createPolicy(
            @RequestBody Policy policy,
            @RequestHeader("X-User-Role") String role
    ) {
        if (!role.equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("ADMIN only");
        }
        return policyService.createPolicy(policy);
    }

    @PutMapping("/api/admin/policies/{id}")
    public Policy updatePolicy(
            @PathVariable Long id,
            @RequestBody Policy policy,
            @RequestHeader("X-User-Role") String role
    ) {
        if (!role.equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("ADMIN only");
        }
        return policyService.updatePolicy(id, policy);
    }

    @DeleteMapping("/api/admin/policies/{id}")
    public String deletePolicy(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role
    ) {
        if (!role.equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("ADMIN only");
        }
        return policyService.deletePolicy(id);
    }

    // ================= PUBLIC =================

    @GetMapping("/api/policies")
    public List<Policy> getAllPolicies() {
        return policyService.getAllPolicies();
    }

    @GetMapping("/api/policies/{id}")
    public Policy getPolicyById(@PathVariable Long id) {
        return policyService.getPolicyById(id);
    }

    // ================ CUSTOMER ================

    @PostMapping("/api/policies/purchase")
    public String purchasePolicy(
            @RequestBody PurchaseRequest request,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role
    ) {
        if (!role.equalsIgnoreCase("CUSTOMER")) {
            throw new RuntimeException("CUSTOMER only");
        }
        return policyService.purchasePolicy(request.getPolicyId(), email);
    }
}