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
    public Policy createPolicy(@RequestBody Policy policy,
                               @RequestHeader("X-User-Role") String role) {

        if (!role.equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Access Denied");
        }

        return policyService.createPolicy(policy);
    }

    // ================================
    // ALL USERS: GET ALL POLICIES
    // ================================
    @GetMapping
    public List<Policy> getAllPolicies() {
        return policyService.getAllPolicies();
    }

    // ================================
    // ALL USERS: GET POLICY BY ID
    // ================================
    @GetMapping("/{id}")
    public Policy getPolicyById(@PathVariable Long id) {
        return policyService.getPolicyById(id);
    }

    // ================================
    // USER: PURCHASE POLICY
    // ================================
    @PostMapping("/purchase")
    public String purchasePolicy(@RequestBody PurchaseRequest request,
                                 @RequestHeader("X-User-Email") String userEmail,
                                 @RequestHeader("X-User-Role") String role) {

        if (!role.equalsIgnoreCase("CUSTOMER")) {
            throw new RuntimeException("Only customers can purchase policies");
        }

        return policyService.purchasePolicy(request.getPolicyId(), userEmail);
    }

    // ================================
    // ADMIN: DELETE POLICY
    // ================================
    @DeleteMapping("/{id}")
    public String deletePolicy(@PathVariable Long id,
                               @RequestHeader("X-User-Role") String role) {

        if (!role.equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Access Denied");
        }

        return policyService.deletePolicy(id);
    }
}