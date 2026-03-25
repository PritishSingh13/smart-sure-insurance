package com.smartsure.adminservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "POLICY-SERVICE")
public interface PolicyClient {

    // CREATE POLICY
    @PostMapping("/internal/policies")
    Object createPolicy(@RequestBody Object policy);

    // UPDATE POLICY
    @PutMapping("/internal/policies/{id}")
    Object updatePolicy(@PathVariable("id") Long id,
                        @RequestBody Object policy);

    // DELETE POLICY
    @DeleteMapping("/internal/policies/{id}")
    String deletePolicy(@PathVariable("id") Long id);
}