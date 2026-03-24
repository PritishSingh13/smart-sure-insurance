package com.smartsure.claimsservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "claims")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String policyNumber;

    private String claimantName;

    private String claimType;

    private Double claimAmount;

    private String status; // PENDING, APPROVED, REJECTED

    private LocalDate claimDate;

    private String documentPath;

    private String createdBy;
}