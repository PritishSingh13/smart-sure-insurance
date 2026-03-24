package com.smartsure.policyservice.controller;

import com.smartsure.policyservice.dto.PurchaseRequest;
import com.smartsure.policyservice.model.Policy;
import com.smartsure.policyservice.service.PolicyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    // ================================
    // ADMIN: CREATE POLICY
    // ================================
    @PostMapping
    public Policy createPolicy(
            @RequestBody Policy policy,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {

        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Access Denied: ADMIN only");
        }

        return policyService.createPolicy(policy);
    }

    // ================================
    // ADMIN: UPDATE POLICY
    // ================================
    @PutMapping("/{id}")
    public Policy updatePolicy(
            @PathVariable Long id,
            @RequestBody Policy policy,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {

        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Access Denied: ADMIN only");
        }

        return policyService.updatePolicy(id, policy);
    }

    // ================================
    // GET ALL POLICIES (PUBLIC)
    // ================================
    @GetMapping
    public List<Policy> getAllPolicies() {
        return policyService.getAllPolicies();
    }

    // ================================
    // GET POLICY BY ID (PUBLIC)
    // ================================
    @GetMapping("/{id}")
    public Policy getPolicyById(@PathVariable Long id) {
        return policyService.getPolicyById(id);
    }

    // ================================
    // CUSTOMER: PURCHASE POLICY
    // ================================
    @PostMapping("/purchase")
    public String purchasePolicy(
            @RequestBody PurchaseRequest request,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {

        if (role == null || !role.equalsIgnoreCase("CUSTOMER")) {
            throw new RuntimeException("Only CUSTOMER can purchase policy");
        }

        if (userEmail == null) {
            throw new RuntimeException("User email missing from request");
        }

        return policyService.purchasePolicy(request.getPolicyId(), userEmail);
    }

    // ================================
    // ADMIN: DELETE POLICY
    // ================================
    @DeleteMapping("/{id}")
    public String deletePolicy(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {

        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Access Denied: ADMIN only");
        }

        return policyService.deletePolicy(id);
    }
}