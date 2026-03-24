package com.smartsure.policyservice.service;

import com.smartsure.policyservice.model.Policy;
import com.smartsure.policyservice.model.PolicyPurchase;
import com.smartsure.policyservice.repository.PolicyRepository;
import com.smartsure.policyservice.repository.PolicyPurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyPurchaseRepository purchaseRepository;

    public PolicyService(PolicyRepository policyRepository,
                         PolicyPurchaseRepository purchaseRepository) {
        this.policyRepository = policyRepository;
        this.purchaseRepository = purchaseRepository;
    }

    // =========================
    // CREATE POLICY
    // =========================
    public Policy createPolicy(Policy policy) {
        return policyRepository.save(policy);
    }

    // =========================
    // UPDATE POLICY (NEW)
    // =========================
    public Policy updatePolicy(Long id, Policy updatedPolicy) {

        Policy existing = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + id));

        existing.setPolicyName(updatedPolicy.getPolicyName());
        existing.setPolicyType(updatedPolicy.getPolicyType());
        existing.setPremium(updatedPolicy.getPremium());
        existing.setDuration(updatedPolicy.getDuration());

        return policyRepository.save(existing);
    }

    // =========================
    // GET ALL POLICIES
    // =========================
    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    // =========================
    // GET POLICY BY ID
    // =========================
    public Policy getPolicyById(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + id));
    }

    // =========================
    // PURCHASE POLICY
    // =========================
    public String purchasePolicy(Long policyId, String userEmail) {

        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + policyId));

        PolicyPurchase purchase = new PolicyPurchase();
        purchase.setPolicyId(policyId);
        purchase.setUserEmail(userEmail);

        purchaseRepository.save(purchase);

        return "Policy purchased successfully";
    }

    // =========================
    // DELETE POLICYY
    // =========================
    public String deletePolicy(Long id) {
        policyRepository.deleteById(id);
        return "Policy deleted successfully";
    }
}