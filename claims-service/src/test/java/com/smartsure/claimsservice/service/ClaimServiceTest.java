package com.smartsure.claimsservice.service;

import com.smartsure.claimsservice.entity.Claim;
import com.smartsure.claimsservice.repository.ClaimRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @InjectMocks
    private ClaimService claimService;

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private MultipartFile file;

    // =========================
    // TEST 1: UPLOAD CLAIM
    // =========================
    @Test
    void testUploadClaim() throws Exception {

        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getInputStream())
                .thenReturn(new ByteArrayInputStream("data".getBytes()));

        Claim claim = new Claim();
        claim.setId(1L);

        when(claimRepository.save(any(Claim.class)))
                .thenReturn(claim);

        Claim result = claimService.uploadClaimWithFile(
                file,
                "POL123",
                "John",
                "HEALTH",
                5000.0,
                "user@gmail.com"
        );

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // =========================
    // TEST 2: INITIATE CLAIM
    // =========================
    @Test
    void testInitiateClaim() {

        Claim claim = new Claim();
        claim.setId(1L);

        when(claimRepository.findById(1L))
                .thenReturn(Optional.of(claim));

        when(claimRepository.save(any(Claim.class)))
                .thenReturn(claim);

        Claim result = claimService.initiateClaim(1L, "user@gmail.com");

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    // =========================
    // TEST 3: GET STATUS
    // =========================
    @Test
    void testGetStatus() {

        Claim claim = new Claim();
        claim.setStatus("APPROVED");

        when(claimRepository.findById(1L))
                .thenReturn(Optional.of(claim));

        String status = claimService.getClaimStatus(1L, "user@gmail.com");

        assertEquals("APPROVED", status);
    }

    // =========================
    // TEST 4: REVIEW CLAIM
    // =========================
    @Test
    void testReviewClaim() {

        Claim claim = new Claim();
        claim.setStatus("PENDING");

        when(claimRepository.findById(1L))
                .thenReturn(Optional.of(claim));

        when(claimRepository.save(any(Claim.class)))
                .thenReturn(claim);

        String result = claimService.reviewClaim(1L, "APPROVED");

        assertNotNull(result);
        assertTrue(result.contains("APPROVED"));
    }

    // =========================
    // TEST 5: GET ALL CLAIMS
    // =========================
    @Test
    void testGetAllClaims() {

        List<Claim> list = Arrays.asList(new Claim(), new Claim());

        when(claimRepository.findAll())
                .thenReturn(list);

        List<Claim> result = claimService.getAllClaims();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // =========================
    // TEST 6: REPORT DATA (FINAL FIX)
    // =========================
    @Test
    void testReportData() {

        Claim c1 = new Claim();
        c1.setStatus("APPROVED");

        Claim c2 = new Claim();
        c2.setStatus("REJECTED");

        List<Claim> mockList = Arrays.asList(c1, c2);

        // IMPORTANT FIX: mock BOTH findAll AND count
        when(claimRepository.findAll()).thenReturn(mockList);
        when(claimRepository.count()).thenReturn(2L);

        Map<String, Long> result = claimService.getReportData();

        System.out.println("REPORT OUTPUT: " + result);

        assertNotNull(result);

        assertEquals(2L, result.get("TOTAL_CLAIMS"));
        assertEquals(1L, result.get("APPROVED"));
        assertEquals(1L, result.get("REJECTED"));
    }
}