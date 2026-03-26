package com.smartsure.claimsservice.controller;

import com.smartsure.claimsservice.dto.ClaimDto;
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
    // USER APIs (SIMPLIFIED FLOW + VALIDATION)
    // =========================

    @PostMapping("/api/claims/upload")
    public ClaimDto uploadClaimWithFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("policyId") Long policyId,
            @RequestParam("claimantName") String claimantName,
            @RequestHeader("X-Auth-User") String userEmail
    ) {

        //  VALIDATIONS
        if (policyId == null || policyId <= 0) {
            throw new RuntimeException("Invalid Policy ID");
        }

        if (claimantName == null || claimantName.trim().isEmpty()) {
            throw new RuntimeException("Claimant name is required");
        }

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File must be uploaded");
        }

        return claimService.uploadClaimWithFile(
                file, policyId, claimantName, userEmail);
    }

    @PostMapping("/api/claims/initiate")
    public ClaimDto initiateClaim(
            @RequestParam("claimNumber") String claimNumber,
            @RequestHeader("X-Auth-User") String userEmail
    ) {

        //  VALIDATION
        if (claimNumber == null || claimNumber.trim().isEmpty()) {
            throw new RuntimeException("Claim Number is required");
        }

        return claimService.initiateClaim(claimNumber, userEmail);
    }

    @GetMapping("/api/claims/status/{claimNumber}")
    public ClaimDto getStatus(@PathVariable String claimNumber) {

        //  VALIDATION
        if (claimNumber == null || claimNumber.trim().isEmpty()) {
            throw new RuntimeException("Claim Number is required");
        }

        return claimService.getClaimStatus(claimNumber);
    }

    // =========================
    // ADMIN APIs (VALIDATED)
    // =========================

    @PutMapping("/api/admin/claims/{claimId}/review")
    public String reviewClaim(@PathVariable Long claimId,
                              @RequestParam String status) {

        //  VALIDATIONS
        if (claimId == null || claimId <= 0) {
            throw new RuntimeException("Invalid Claim ID");
        }

        if (status == null ||
                (!status.equalsIgnoreCase("APPROVED") &&
                        !status.equalsIgnoreCase("REJECTED"))) {
            throw new RuntimeException("Status must be APPROVED or REJECTED");
        }

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
    // INTERNAL APIs
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