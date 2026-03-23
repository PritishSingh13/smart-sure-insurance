package com.smartsure.claimsservice.controller;

import com.smartsure.claimsservice.entity.Claim;
import com.smartsure.claimsservice.service.ClaimService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    // ==============================
    // USER: UPLOAD CLAIM
    // ==============================
    @PostMapping("/upload")
    @PreAuthorize("#role == 'CUSTOMER' or #role == 'ADMIN'")
    public Claim uploadClaim(@RequestBody Claim claim,
                             @RequestHeader("X-User-Role") String role) {
        return claimService.uploadClaim(claim);
    }

    // ==============================
    // USER: INITIATE CLAIM PROCESS
    // ==============================
    @PostMapping("/initiate")
    @PreAuthorize("#role == 'CUSTOMER' or #role == 'ADMIN'")
    public Claim initiateClaim(@RequestParam Long claimId,
                               @RequestHeader("X-User-Role") String role) {
        return claimService.initiateClaim(claimId);
    }

    // ==============================
    // USER: GET CLAIM STATUS
    // ==============================
    @GetMapping("/status/{claimId}")
    @PreAuthorize("#role == 'CUSTOMER' or #role == 'ADMIN'")
    public String getClaimStatus(@PathVariable Long claimId,
                                 @RequestHeader("X-User-Role") String role) {
        return claimService.getClaimStatus(claimId);
    }

    // ==============================
    // ADMIN: REVIEW CLAIM
    // ==============================
    @PutMapping("/admin/review/{claimId}")
    @PreAuthorize("#role == 'ADMIN'")
    public Claim reviewClaim(@PathVariable Long claimId,
                             @RequestParam String status,
                             @RequestHeader("X-User-Role") String role) {
        return claimService.reviewClaim(claimId, status);
    }

    // ==============================
    // ADMIN: GET ALL CLAIMS
    // ==============================
    @GetMapping
    @PreAuthorize("#role == 'ADMIN'")
    public List<Claim> getAllClaims(@RequestHeader("X-User-Role") String role) {
        return claimService.getAllClaims();
    }
}