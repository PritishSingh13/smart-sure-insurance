package com.smartsure.claimsservice.repository;

import com.smartsure.claimsservice.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {


    List<Claim> findByCreatedBy(String createdBy);


    Optional<Claim> findByClaimNumber(String claimNumber);
}