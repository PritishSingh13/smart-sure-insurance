package com.smartsure.adminservice.controller;

import com.smartsure.adminservice.client.ClaimsClient;
import com.smartsure.adminservice.client.PolicyClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ClaimsClient claimsClient;
    private final PolicyClient policyClient;

    public AdminController(ClaimsClient claimsClient,
                           PolicyClient policyClient) {
        this.claimsClient = claimsClient;
        this.policyClient = policyClient;
    }

    // =========================
    // ADMIN: REVIEW CLAIM
    // =========================
    @PutMapping("/claims/{claimId}/review")
    public String reviewClaim(@PathVariable Long claimId,
                              @RequestParam String status) {

        return claimsClient.reviewClaim(claimId, status);
    }

    // =========================
    // ADMIN: GET ALL CLAIMS
    // =========================
    @GetMapping("/claims")
    public List<Object> getAllClaims() {
        return claimsClient.getAllClaims();
    }

    // =========================
    // ADMIN: REPORTS
    // =========================
    @GetMapping("/reports")
    public Map<String, Long> getReports() {
        return claimsClient.getReports();
    }

    // =========================
    // ADMIN: CREATE POLICY
    // =========================
    @PostMapping("/policies")
    public Object createPolicy(@RequestBody Object policy) {
        return policyClient.createPolicy(policy);
    }

    // =========================
    // ADMIN: UPDATE POLICY
    // =========================
    @PutMapping("/policies/{id}")
    public Object updatePolicy(@PathVariable Long id,
                               @RequestBody Object policy) {
        return policyClient.updatePolicy(id, policy);
    }

    // =========================
    // ADMIN: DELETE POLICY
    // =========================
    @DeleteMapping("/policies/{id}")
    public String deletePolicy(@PathVariable Long id) {
        return policyClient.deletePolicy(id);
    }
}