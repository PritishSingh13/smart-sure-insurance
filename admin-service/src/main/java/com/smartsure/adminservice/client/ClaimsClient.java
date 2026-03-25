package com.smartsure.adminservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "CLAIMS-SERVICE")
public interface ClaimsClient {

    // REVIEW CLAIM
    @PutMapping("/internal/claims/review/{id}")
    String reviewClaim(@PathVariable("id") Long id,
                       @RequestParam("status") String status);

    // GET ALL CLAIMS
    @GetMapping("/internal/claims")
    List<Object> getAllClaims();

    // GET REPORTS
    @GetMapping("/internal/claims/reports")
    Map<String, Long> getReports();
}