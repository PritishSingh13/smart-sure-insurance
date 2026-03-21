package com.smartsure.policyservice.service;

import com.smartsure.policyservice.model.Policy;
import com.smartsure.policyservice.repository.PolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    // CREATE
    public Policy createPolicy(Policy policy) {
        return policyRepository.save(policy);
    }

    // GET ALL
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    // GET BY ID
    public Policy getPolicyById(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
    }

    // DELETE
    public String deletePolicy(Long id) {
        policyRepository.deleteById(id);
        return "Policy deleted successfully";
    }
}