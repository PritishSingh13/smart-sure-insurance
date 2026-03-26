package com.smartsure.claimsservice.service;

import com.smartsure.claimsservice.dto.ClaimDto;
import com.smartsure.claimsservice.entity.Claim;
import com.smartsure.claimsservice.repository.ClaimRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final String uploadDir = "uploads/";

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    // =========================
    // UPLOAD CLAIM (SIMPLIFIED)
    // =========================
    public ClaimDto uploadClaimWithFile(MultipartFile file,
                                        Long policyId,
                                        String claimantName,
                                        String userEmail) {

        Claim claim = new Claim();

        claim.setPolicyId(policyId);
        claim.setClaimantName(claimantName);

        // ✅ AUTO VALUES
        claim.setClaimType("GENERAL");
        claim.setClaimAmount(0.0);

        claim.setStatus("UPLOADED");
        claim.setClaimDate(LocalDate.now());
        claim.setCreatedBy(userEmail);

        if (file != null && !file.isEmpty()) {
            try {
                Path folderPath = Paths.get(uploadDir);
                if (!Files.exists(folderPath)) Files.createDirectories(folderPath);

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path filePath = folderPath.resolve(fileName);

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                claim.setDocumentPath(filePath.toString());

            } catch (IOException e) {
                throw new RuntimeException("File upload failed: " + e.getMessage());
            }
        }

        Claim saved = claimRepository.save(claim);
        return convertToDto(saved);
    }

    // =========================
    // INITIATE CLAIM
    // =========================
    public ClaimDto initiateClaim(String claimNumber, String userEmail) {
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        claim.setStatus("SUBMITTED");
        return convertToDto(claimRepository.save(claim));
    }

    // =========================
    public ClaimDto getClaimStatus(String claimNumber) {
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        return convertToDto(claim);
    }

    // =========================
    public String reviewClaim(Long id, String status) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        claim.setStatus(status.toUpperCase());
        claimRepository.save(claim);

        return "Claim " + status.toUpperCase();
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public Map<String, Long> getReportData() {
        Map<String, Long> report = new HashMap<>();

        long total = claimRepository.count();
        long approved = claimRepository.findAll().stream()
                .filter(c -> "APPROVED".equalsIgnoreCase(c.getStatus()))
                .count();
        long rejected = claimRepository.findAll().stream()
                .filter(c -> "REJECTED".equalsIgnoreCase(c.getStatus()))
                .count();

        report.put("TOTAL_CLAIMS", total);
        report.put("APPROVED", approved);
        report.put("REJECTED", rejected);

        return report;
    }

    private ClaimDto convertToDto(Claim claim) {
        ClaimDto dto = new ClaimDto();
        dto.setClaimNumber(claim.getClaimNumber());
        dto.setPolicyId(claim.getPolicyId());
        dto.setClaimantName(claim.getClaimantName());
        dto.setStatus(claim.getStatus());
        return dto;
    }
}