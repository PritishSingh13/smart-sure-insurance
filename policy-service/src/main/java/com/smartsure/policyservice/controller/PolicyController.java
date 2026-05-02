package com.smartsure.policyservice.controller;

import com.smartsure.policyservice.dto.PurchaseRequest;
import com.smartsure.policyservice.model.Policy;
import com.smartsure.policyservice.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class PolicyController {


    //injected Policyservice to use the logics inside it
    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    // ================= ADMIN =================

    @PostMapping("/api/admin/policies")
    public Object createPolicy(
            @Valid @RequestBody Object input,
            @RequestHeader("X-User-Role") String role
    ) {
        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ADMIN only");
        }

        return policyService.createPolicy(input);
    }

    @PutMapping("/api/admin/policies/{id}")
    public Policy updatePolicy(
            @PathVariable Long id,
            @Valid @RequestBody Policy policy,
            @RequestHeader("X-User-Role") String role
    ) {
        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ADMIN only");
        }
        return policyService.updatePolicy(id, policy);
    }

    @DeleteMapping("/api/admin/policies/{id}")
    public String deletePolicy(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role
    ) {
        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ADMIN only");
        }
        return policyService.deletePolicy(id);
    }

    // ================= INTERNAL ADMIN-SERVICE APIs =================

    @GetMapping("/internal/policies")
    public List<Policy> getAllPoliciesInternal() {
        return policyService.getAllPolicies();
    }

    @PostMapping("/internal/policies")
    public Object createPolicyInternal(@Valid @RequestBody Object input) {
        return policyService.createPolicy(input);
    }

    @PutMapping("/internal/policies/{id}")
    public Policy updatePolicyInternal(@PathVariable Long id,
                                       @Valid @RequestBody Policy policy) {
        return policyService.updatePolicy(id, policy);
    }

    @DeleteMapping("/internal/policies/{id}")
    public String deletePolicyInternal(@PathVariable Long id) {
        return policyService.deletePolicy(id);
    }

    // ================ PUBLIC ================

    @GetMapping("/api/policies")
    public List<Policy> getAllPolicies() {
        return policyService.getAllPolicies();
    }

    @GetMapping("/api/policies/{id}")
    public Policy getPolicyById(@PathVariable Long id) {
        return policyService.getPolicyById(id);
    }

    @GetMapping("/api/policies/my")
    public List<Policy> getMyPolicies(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role
    ) {
        if (role == null || !role.equalsIgnoreCase("CUSTOMER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "CUSTOMER only");
        }

        return policyService.getPoliciesForUser(email);
    }

    // ================ CUSTOMER ================

    @PostMapping("/api/policies/purchase")
    public String purchasePolicy(
            @Valid @RequestBody PurchaseRequest request,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role
    ) {
        if (role == null || !role.equalsIgnoreCase("CUSTOMER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "CUSTOMER only");
        }

        return policyService.purchasePolicy(request.getPolicyId(), email);
    }
}
