package com.smartsure.claimsservice.controller;

import com.smartsure.claimsservice.entity.Claim;
import com.smartsure.claimsservice.service.ClaimService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    // =========================
    // USER: UPLOAD CLAIM (WITH FILE)
    // =========================
    @PostMapping("/upload")
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

    // =========================
    // USER: INITIATE CLAIM
    // =========================
    @PostMapping("/initiate")
    public Claim initiateClaim(@RequestParam Long claimId,
                               @RequestHeader("X-Auth-User") String userEmail) {
        return claimService.initiateClaim(claimId, userEmail);
    }

    // =========================
    // USER: GET STATUS
    // =========================
    @GetMapping("/status/{claimId}")
    public String getStatus(@PathVariable Long claimId,
                            @RequestHeader("X-Auth-User") String userEmail) {
        return claimService.getClaimStatus(claimId, userEmail);
    }

    // =========================
    // ADMIN: REVIEW CLAIM
    // =========================
    @PutMapping("/admin/review/{claimId}")
    public String reviewClaim(@PathVariable Long claimId,
                              @RequestParam String status) {
        return claimService.reviewClaim(claimId, status);
    }

    // =========================
    // ADMIN: GET ALL CLAIMS
    // =========================
    @GetMapping
    public List<Claim> getAll() {
        return claimService.getAllClaims();
    }

    // =========================
    // ADMIN: REPORTS
    // =========================
    @GetMapping("/admin/reports")
    public Map<String, Long> getReports() {
        return claimService.getReportData();
    }
}