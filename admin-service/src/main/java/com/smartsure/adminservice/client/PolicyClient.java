package com.smartsure.adminservice.client;

import com.smartsure.adminservice.dto.PolicyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "POLICY-SERVICE")
public interface PolicyClient {

    // CREATE POLICY
    @PostMapping("/internal/policies")
    PolicyDto createPolicy(@RequestBody PolicyDto policy);

    // UPDATE POLICY
    @PutMapping("/internal/policies/{id}")
    PolicyDto updatePolicy(@PathVariable("id") Long id,
                           @RequestBody PolicyDto policy);

    // DELETE POLICY
    @DeleteMapping("/internal/policies/{id}")
    String deletePolicy(@PathVariable("id") Long id);
}