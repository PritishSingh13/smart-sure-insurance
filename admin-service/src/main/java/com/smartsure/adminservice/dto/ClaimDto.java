package com.smartsure.adminservice.dto;

import lombok.Data;

@Data
public class ClaimDto {

    private Long id;
    private String policyNumber;
    private String claimantName;
    private String status;
    private Double claimAmount;
}