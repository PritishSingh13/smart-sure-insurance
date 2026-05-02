package com.smartsure.adminservice.controller;

import com.smartsure.adminservice.client.ClaimsClient;
import com.smartsure.adminservice.client.PolicyClient;
import com.smartsure.adminservice.dto.ClaimDto;
import com.smartsure.adminservice.dto.PolicyDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
                              @RequestParam String status,
                              @RequestHeader(value = "X-Auth-Role", required = false) String role) {
        requireAdmin(role);

        return claimsClient.reviewClaim(claimId, status);
    }

    // =========================
    // ADMIN: GET ALL CLAIMS
    // =========================
    @GetMapping("/claims")
    public List<ClaimDto> getAllClaims(@RequestHeader(value = "X-Auth-Role", required = false) String role) {
        requireAdmin(role);
        return claimsClient.getAllClaims();
    }

    @GetMapping("/claims/{claimId}/document")
    public ResponseEntity<byte[]> getClaimDocument(@PathVariable Long claimId,
                                                   @RequestHeader(value = "X-Auth-Role", required = false) String role) {
        requireAdmin(role);
        return claimsClient.getClaimDocument(claimId);
    }

    // =========================
    // ADMIN: REPORTS
    // =========================
    @GetMapping("/reports")
    public Map<String, Long> getReports(@RequestHeader(value = "X-Auth-Role", required = false) String role) {
        requireAdmin(role);
        return claimsClient.getReports();
    }

    // =========================
    // ADMIN: GET POLICIES
    // =========================
    @GetMapping("/policies")
    public List<PolicyDto> getPolicies(@RequestHeader(value = "X-Auth-Role", required = false) String role) {
        requireAdmin(role);
        return policyClient.getAllPolicies();
    }

    // =========================
    // ADMIN: CREATE POLICY
    // =========================
    @PostMapping("/policies")
    public PolicyDto createPolicy(@RequestBody PolicyDto policy,
                                  @RequestHeader(value = "X-Auth-Role", required = false) String role) {
        requireAdmin(role);
        return policyClient.createPolicy(policy);
    }

    // =========================
    // ADMIN: UPDATE POLICY
    // =========================
    @PutMapping("/policies/{id}")
    public PolicyDto updatePolicy(@PathVariable Long id,
                                  @RequestBody PolicyDto policy,
                                  @RequestHeader(value = "X-Auth-Role", required = false) String role) {
        requireAdmin(role);
        return policyClient.updatePolicy(id, policy);
    }

    // =========================
    // ADMIN: DELETE POLICY
    // =========================
    @DeleteMapping("/policies/{id}")
    public String deletePolicy(@PathVariable Long id,
                               @RequestHeader(value = "X-Auth-Role", required = false) String role) {
        requireAdmin(role);
        return policyClient.deletePolicy(id);
    }

    private void requireAdmin(String role) {
        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ADMIN only");
        }
    }
}
