package com.smartsure.claimsservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

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
    private Long id;  // internal DB ID

    @Column(unique = true)
    private String claimNumber; // new: unique number for customer

    private Long PolicyId;

    private String claimantName;

    private String claimType;

    private Double claimAmount;

    private String status; // PENDING, UPLOADED, SUBMITTED, APPROVED, REJECTED

    private LocalDate claimDate;

    private String documentPath;

    private String createdBy;

    @PrePersist
    public void generateClaimNumber() {
        if (this.claimNumber == null || this.claimNumber.isEmpty()) {
            this.claimNumber = "CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}