package com.smartsure.claimsservice.service;

import com.smartsure.claimsservice.entity.Claim;
import com.smartsure.claimsservice.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    // CREATE CLAIM
    public Claim createClaim(Claim claim) {
        claim.setStatus("PENDING");
        claim.setClaimDate(LocalDate.now());
        return claimRepository.save(claim);
    }

    // GET ALL CLAIMS
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    // GET CLAIM BY ID
    public Optional<Claim> getClaimById(Long id) {
        return claimRepository.findById(id);
    }

    // GET BY POLICY NUMBER
    public List<Claim> getByPolicyNumber(String policyNumber) {
        return claimRepository.findByPolicyNumber(policyNumber);
    }
}