package com.smartsure.claimsservice.controller;

import com.smartsure.claimsservice.dto.ClaimDto;
import com.smartsure.claimsservice.entity.Claim;
import com.smartsure.claimsservice.service.ClaimService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
public class ClaimController {


    //injected the claimservice to call the busineess logic
    private final ClaimService claimService;

    //constructor injection of claim service to use claimservice
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

    @GetMapping("/api/claims/my")
    public List<ClaimDto> getMyClaims(@RequestHeader("X-Auth-User") String userEmail) {
        return claimService.getClaimsForUser(userEmail);
    }

    @GetMapping("/api/claims/{claimNumber}/document")
    public ResponseEntity<Resource> getMyClaimDocument(@PathVariable String claimNumber,
                                                       @RequestHeader("X-Auth-User") String userEmail) throws Exception {
        Claim claim = claimService.getClaimForUserByNumber(claimNumber, userEmail);
        return buildClaimDocumentResponse(claim);
    }

    // =========================
    // ADMIN APIs (VALIDATED)
    // =========================

    @PutMapping("/api/admin/claims/{claimId}/review")
    public String reviewClaim(@PathVariable Long claimId,
                              @RequestParam String status,
                              @RequestHeader("X-Auth-Role") String role) {
        requireAdmin(role);

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
    public List<Claim> getAllClaims(@RequestHeader("X-Auth-Role") String role) {
        requireAdmin(role);
        return claimService.getAllClaims();
    }

    @GetMapping("/api/admin/claims/{claimId}/document")
    public ResponseEntity<Resource> getClaimDocument(@PathVariable Long claimId,
                                                     @RequestHeader("X-Auth-Role") String role) throws Exception {
        requireAdmin(role);
        return buildClaimDocumentResponse(claimId);
    }

    private ResponseEntity<Resource> buildClaimDocumentResponse(Long claimId) throws Exception {
        Claim claim = claimService.getClaimById(claimId);
        return buildClaimDocumentResponse(claim);
    }

    private ResponseEntity<Resource> buildClaimDocumentResponse(Claim claim) throws Exception {
        if (claim.getDocumentPath() == null || claim.getDocumentPath().isBlank()) {
            throw new RuntimeException("No document found for this claim");
        }

        Path path = Paths.get(claim.getDocumentPath()).toAbsolutePath().normalize();
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("Document file not found");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @GetMapping("/api/admin/reports")
    public Map<String, Long> getReports(@RequestHeader("X-Auth-Role") String role) {
        requireAdmin(role);
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

    @GetMapping("/internal/claims/{claimId}/document")
    public ResponseEntity<byte[]> getClaimDocumentInternal(@PathVariable Long claimId) throws Exception {
        Claim claim = claimService.getClaimById(claimId);
        
        if (claim.getDocumentPath() == null || claim.getDocumentPath().isBlank()) {
            throw new RuntimeException("No document found for this claim");
        }

        Path path = Paths.get(claim.getDocumentPath()).toAbsolutePath().normalize();
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("Document file not found");
        }

        byte[] documentBytes = resource.getContentAsByteArray();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(documentBytes);
    }

    private void requireAdmin(String role) {
        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ADMIN only");
        }
    }
}
