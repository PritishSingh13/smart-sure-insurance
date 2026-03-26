package com.smartsure.policyservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartsure.policyservice.model.Policy;
import com.smartsure.policyservice.model.PolicyPurchase;
import com.smartsure.policyservice.repository.PolicyRepository;
import com.smartsure.policyservice.repository.PolicyPurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    // CREATE POLICY (SINGLE + MULTIPLE SUPPORT)
    // =========================
    public Object createPolicy(Object requestBody) {

        ObjectMapper mapper = new ObjectMapper();

        // If ARRAY → multiple policies
        if (requestBody instanceof List) {

            List<?> rawList = (List<?>) requestBody;
            List<Policy> savedPolicies = new ArrayList<>();

            for (Object obj : rawList) {
                Policy policy = mapper.convertValue(obj, Policy.class);
                savedPolicies.add(policyRepository.save(policy));
            }

            return savedPolicies;
        }

        // If SINGLE → one policy
        Policy policy = mapper.convertValue(requestBody, Policy.class);
        return policyRepository.save(policy);
    }

    // =========================
    // UPDATE POLICY
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
    // PURCHASE POLICY (UPDATED FLOW)
    // =========================
    public String purchasePolicy(Long policyId, String userEmail) {

        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + policyId));

        PolicyPurchase purchase = new PolicyPurchase();
        purchase.setPolicyId(policyId);
        purchase.setUserEmail(userEmail);

        purchaseRepository.save(purchase);

        return "Policy Purchased Successfully!\n" +
                "Policy ID: " + policy.getId() + "\n" +
                "Policy Name: " + policy.getPolicyName() + "\n" +
                "User Email: " + userEmail + "\n" +
                "👉 Use this Policy ID for Claims: " + policy.getId();
    }

    // =========================
    // DELETE POLICY
    // =========================
    public String deletePolicy(Long id) {
        policyRepository.deleteById(id);
        return "Policy deleted successfully";
    }
}