package com.smartsure.claimsservice.repository;

import com.smartsure.claimsservice.entity.Claim;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimRepositoryTest {

    @Mock
    private ClaimRepository claimRepository;

    // =========================
    // TEST 1: SAVE CLAIM
    // =========================
    @Test
    void testSave() {

        Claim claim = new Claim();
        claim.setId(1L);

        when(claimRepository.save(any(Claim.class)))
                .thenReturn(claim);

        Claim result = claimRepository.save(claim);

        assertNotNull(result);
    }

    // =========================
    // TEST 2: FIND BY ID
    // =========================
    @Test
    void testFindById() {

        Claim claim = new Claim();
        claim.setId(1L);

        when(claimRepository.findById(1L))
                .thenReturn(Optional.of(claim));

        Optional<Claim> result = claimRepository.findById(1L);

        assertTrue(result.isPresent());
    }

    // =========================
    // TEST 3: FIND ALL
    // =========================
    @Test
    void testFindAll() {

        when(claimRepository.findAll())
                .thenReturn(Arrays.asList(new Claim(), new Claim()));

        List<Claim> result = claimRepository.findAll();

        assertEquals(2, result.size());
    }
}