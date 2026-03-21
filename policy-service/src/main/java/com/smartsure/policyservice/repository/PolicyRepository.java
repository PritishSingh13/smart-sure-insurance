package com.smartsure.policyservice.repository;

import com.smartsure.policyservice.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
}