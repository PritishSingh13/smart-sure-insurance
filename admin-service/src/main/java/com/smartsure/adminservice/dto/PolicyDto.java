package com.smartsure.adminservice.dto;

import lombok.Data;

@Data
public class PolicyDto {

    private Long id;
    private String policyName;
    private String policyType;
    private Double premium;
    private Double coverageAmount;
}