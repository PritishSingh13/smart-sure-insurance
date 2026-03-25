package com.smartsure.claimsservice.service;

import com.smartsure.claimsservice.entity.Claim;
import com.smartsure.claimsservice.repository.ClaimRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final String uploadDir = "uploads/";

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    // =========================
    // UPLOAD CLAIM WITH FILE + USER
    // =========================
    public Claim uploadClaimWithFile(MultipartFile file,
                                     String policyNumber,
                                     String claimantName,
                                     String claimType,
                                     Double claimAmount,
                                     String userEmail) {

        Claim claim = new Claim();
        claim.setPolicyNumber(policyNumber);
        claim.setClaimantName(claimantName);
        claim.setClaimType(claimType);
        claim.setClaimAmount(claimAmount);

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

        return claimRepository.save(claim);
    }

    // =========================
    // INITIATE CLAIMM
    // =========================
    public Claim initiateClaim(Long id, String userEmail) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        claim.setStatus("SUBMITTED");
        return claimRepository.save(claim);
    }

    // =========================
    // GET CLAIM STATUS
    // =========================
    public String getClaimStatus(Long id, String userEmail) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        return claim.getStatus();
    }

    // =========================
    // REVIEW CLAIM (ADMIN)
    // =========================
    public String reviewClaim(Long id, String status) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        if (!status.equalsIgnoreCase("APPROVED") &&
                !status.equalsIgnoreCase("REJECTED")) {
            throw new RuntimeException("Invalid status");
        }

        claim.setStatus(status.toUpperCase());
        claimRepository.save(claim);

        return "Claim " + status.toUpperCase();
    }

    // =========================
    // GET ALL CLAIMS
    // =========================
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    // =========================
    // GET CLAIM REPORT DATA
    // =========================
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
}