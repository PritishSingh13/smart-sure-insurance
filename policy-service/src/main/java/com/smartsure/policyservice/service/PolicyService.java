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

        if (requestBody == null) {
            throw new RuntimeException("Request body cannot be empty");
        }

        ObjectMapper mapper = new ObjectMapper();

        // MULTIPLE
        if (requestBody instanceof List) {

            List<?> rawList = (List<?>) requestBody;

            if (rawList.isEmpty()) {
                throw new RuntimeException("Policy list cannot be empty");
            }

            List<Policy> savedPolicies = new ArrayList<>();

            for (Object obj : rawList) {
                Policy policy = mapper.convertValue(obj, Policy.class);
                savedPolicies.add(policyRepository.save(policy));
            }

            return savedPolicies;
        }

        // SINGLE
        Policy policy = mapper.convertValue(requestBody, Policy.class);
        return policyRepository.save(policy);
    }

    // =========================
    // UPDATE POLICY
    // =========================
    public Policy updatePolicy(Long id, Policy updatedPolicy) {

        if (id == null) {
            throw new RuntimeException("Policy ID cannot be null");
        }

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

        if (id == null) {
            throw new RuntimeException("Policy ID cannot be null");
        }

        return policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found with id: " + id));
    }

    // =========================
    // PURCHASE POLICY
    // =========================
    public String purchasePolicy(Long policyId, String userEmail) {

        if (policyId == null) {
            throw new RuntimeException("Policy ID is required");
        }

        if (userEmail == null || userEmail.isEmpty()) {
            throw new RuntimeException("User email is required");
        }

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

        if (id == null) {
            throw new RuntimeException("Policy ID cannot be null");
        }

        policyRepository.deleteById(id);
        return "Policy deleted successfully";
    }
}