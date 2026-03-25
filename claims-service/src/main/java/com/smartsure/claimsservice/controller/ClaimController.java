package com.smartsure.claimsservice.controller;

import com.smartsure.claimsservice.entity.Claim;
import com.smartsure.claimsservice.service.ClaimService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    // =========================
    // USER APIs
    // =========================

    @PostMapping("/api/claims/upload")
    public Claim uploadClaimWithFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("policyNumber") String policyNumber,
            @RequestParam("claimantName") String claimantName,
            @RequestParam("claimType") String claimType,
            @RequestParam("claimAmount") Double claimAmount,
            @RequestHeader("X-Auth-User") String userEmail
    ) {
        return claimService.uploadClaimWithFile(
                file, policyNumber, claimantName, claimType, claimAmount, userEmail);
    }

    @PostMapping("/api/claims/initiate")
    public Claim initiateClaim(@RequestParam Long claimId,
                               @RequestHeader("X-Auth-User") String userEmail) {
        return claimService.initiateClaim(claimId, userEmail);
    }

    @GetMapping("/api/claims/status/{claimId}")
    public String getStatus(@PathVariable Long claimId,
                            @RequestHeader("X-Auth-User") String userEmail) {
        return claimService.getClaimStatus(claimId, userEmail);
    }

    // =========================
    // ADMIN APIs (FOR GATEWAY)
    // =========================

    @PutMapping("/api/admin/claims/{claimId}/review")
    public String reviewClaim(@PathVariable Long claimId,
                              @RequestParam String status) {
        return claimService.reviewClaim(claimId, status);
    }

    @GetMapping("/api/admin/claims")
    public List<Claim> getAllClaims() {
        return claimService.getAllClaims();
    }

    @GetMapping("/api/admin/reports")
    public Map<String, Long> getReports() {
        return claimService.getReportData();
    }

    // =========================
    // INTERNAL APIs (FOR FEIGN)
    // =========================

    @PutMapping("/internal/claims/review/{claimId}")
    public String reviewInternal(@PathVariable Long claimId,
                                 @RequestParam String status) {
        return claimService.reviewClaim(claimId, status);
    }

    @GetMapping("/internal/claims")
    public List<Claim> getAllInternal() {
        return claimService.getAllClaims();
    }

    @GetMapping("/internal/claims/reports")
    public Map<String, Long> getReportsInternal() {
        return claimService.getReportData();
    }
}