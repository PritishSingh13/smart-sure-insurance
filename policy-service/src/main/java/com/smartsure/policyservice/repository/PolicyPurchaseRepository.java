package com.smartsure.policyservice.repository;

import com.smartsure.policyservice.model.PolicyPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyPurchaseRepository extends JpaRepository<PolicyPurchase, Long> {
}