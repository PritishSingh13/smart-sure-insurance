package com.smartsure.claimsservice.dto;

import lombok.Data;

@Data
public class ClaimDto {

    private String claimNumber;   // new field to replace DB id
    private Long policyId;
    private String claimantName;
    private String status;        // PENDING, UPLOADED, SUBMITTED, APPROVED, REJECTED
}