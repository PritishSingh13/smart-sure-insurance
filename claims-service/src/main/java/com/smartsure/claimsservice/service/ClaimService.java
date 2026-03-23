package com.smartsure.claimsservice.service;

import com.smartsure.claimsservice.entity.Claim;
import com.smartsure.claimsservice.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    // ==========================
    // UPLOAD CLAIM (USER)
    // ==========================
    public Claim uploadClaim(Claim claim) {
        claim.setStatus("UPLOADED");
        claim.setClaimDate(LocalDate.now());
        return claimRepository.save(claim);
    }

    // ==========================
    // INITIATE CLAIM (USER)
    // ==========================
    public Claim initiateClaim(Long id) {

        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        claim.setStatus("INITIATED");

        return claimRepository.save(claim);
    }

    // ==========================
    // GET CLAIM STATUS
    // ==========================
    public String getClaimStatus(Long id) {

        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        return claim.getStatus();
    }

    // ==========================
    // ADMIN: APPROVE / REJECT
    // ==========================
    public Claim reviewClaim(Long id, String status) {

        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        claim.setStatus(status); // APPROVED / REJECTED

        return claimRepository.save(claim);
    }

    // ==========================
    // ADMIN: GET ALL CLAIMS
    // ==========================
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    // ==========================
    // OPTIONAL EXTENSION (KEEP FOR FUTURE)
    // ==========================
    public List<Claim> getByPolicyNumber(String policyNumber) {
        return claimRepository.findByPolicyNumber(policyNumber);
    }
}