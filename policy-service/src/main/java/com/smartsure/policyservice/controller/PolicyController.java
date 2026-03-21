package com.smartsure.policyservice.controller;

import com.smartsure.policyservice.model.Policy;
import com.smartsure.policyservice.service.PolicyService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    // CREATE POLICY
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Policy createPolicy(@RequestBody Policy policy) {
        return policyService.createPolicy(policy);
    }

    // GET ALL POLICIES
    @GetMapping
    public List<Policy> getAllPolicies() {
        return policyService.getAllPolicies();
    }

    // GET POLICY BY ID
    @GetMapping("/{id}")
    public Policy getPolicyById(@PathVariable Long id) {
        return policyService.getPolicyById(id);
    }

    // DELETE POLICY
    @DeleteMapping("/{id}")
    public String deletePolicy(@PathVariable Long id) {
        return policyService.deletePolicy(id);
    }
}