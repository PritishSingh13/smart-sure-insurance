package com.smartsure.claimsservice.controller;

import com.smartsure.claimsservice.entity.Claim;
import com.smartsure.claimsservice.service.ClaimService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    // ✅ CREATE CLAIM (ONLY ADMIN)
    @PostMapping
    public Object createClaim(@RequestBody Claim claim,
                              HttpServletRequest request) {

        String role = request.getHeader("X-User-Role");

        if (role == null || !role.equals("ADMIN")) {
            return "❌ Access Denied: Only ADMIN can create claims";
        }

        return claimService.createClaim(claim);
    }

    // ✅ GET ALL CLAIMS (USER + ADMIN)
    @GetMapping
    public List<Claim> getAllClaims() {
        return claimService.getAllClaims();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public Optional<Claim> getClaimById(@PathVariable Long id) {
        return claimService.getClaimById(id);
    }

    // GET BY POLICY NUMBER
    @GetMapping("/policy/{policyNumber}")
    public List<Claim> getByPolicyNumber(@PathVariable String policyNumber) {
        return claimService.getByPolicyNumber(policyNumber);
    }
}