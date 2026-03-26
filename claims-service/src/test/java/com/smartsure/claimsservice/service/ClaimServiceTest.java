package com.smartsure.claimsservice.service;

import com.smartsure.claimsservice.dto.ClaimDto;
import com.smartsure.claimsservice.entity.Claim;
import com.smartsure.claimsservice.repository.ClaimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @InjectMocks
    private ClaimService claimService;

    private Claim claim;

    @BeforeEach
    void setUp() {
        claim = new Claim();
        claim.setId(1L);
        claim.setClaimNumber("CLM-123");
        claim.setPolicyId(101L);
        claim.setClaimantName("John Doe");
        claim.setStatus("UPLOADED");
        claim.setCreatedBy("john@example.com");
    }

    @Test
    void testUploadClaimWithFile_Success() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "dummy content".getBytes());

        when(claimRepository.save(any(Claim.class))).thenReturn(claim);

        ClaimDto dto = claimService.uploadClaimWithFile(file, 101L, "John Doe", "john@example.com");

        assertNotNull(dto);
        assertEquals("CLM-123", dto.getClaimNumber());
        assertEquals("UPLOADED", dto.getStatus());
        verify(claimRepository, times(1)).save(any(Claim.class));
    }

    @Test
    void testUploadClaimWithFile_InvalidFileType() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "dummy content".getBytes());

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> claimService.uploadClaimWithFile(file, 101L, "John Doe", "john@example.com"));

        assertEquals("Only PDF files are allowed", exception.getMessage());
        verify(claimRepository, never()).save(any(Claim.class));
    }

    @Test
    void testUploadClaimWithFile_NoFile() {
        when(claimRepository.save(any(Claim.class))).thenReturn(claim);

        ClaimDto dto = claimService.uploadClaimWithFile(null, 101L, "John Doe", "john@example.com");

        assertNotNull(dto);
        assertEquals("UPLOADED", dto.getStatus());
        verify(claimRepository, times(1)).save(any(Claim.class));
    }

    @Test
    void testInitiateClaim_Success() {
        claim.setStatus("SUBMITTED");
        when(claimRepository.findByClaimNumber(anyString())).thenReturn(Optional.of(claim));
        when(claimRepository.save(any(Claim.class))).thenReturn(claim);

        ClaimDto dto = claimService.initiateClaim("CLM-123", "john@example.com");

        assertNotNull(dto);
        assertEquals("SUBMITTED", dto.getStatus());
        verify(claimRepository, times(1)).save(any(Claim.class));
    }

    @Test
    void testInitiateClaim_Unauthorized() {
        when(claimRepository.findByClaimNumber(anyString())).thenReturn(Optional.of(claim));

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> claimService.initiateClaim("CLM-123", "hacker@example.com"));

        assertEquals("Unauthorized access", exception.getMessage());
    }

    @Test
    void testGetClaimStatus_Success() {
        when(claimRepository.findByClaimNumber(anyString())).thenReturn(Optional.of(claim));

        ClaimDto dto = claimService.getClaimStatus("CLM-123");

        assertNotNull(dto);
        assertEquals("UPLOADED", dto.getStatus());
    }

    @Test
    void testGetClaimStatus_NotFound() {
        when(claimRepository.findByClaimNumber(anyString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> claimService.getClaimStatus("CLM-999"));

        assertEquals("Claim not found", exception.getMessage());
    }

    @Test
    void testReviewClaim_Approve() {
        when(claimRepository.findById(anyLong())).thenReturn(Optional.of(claim));
        when(claimRepository.save(any(Claim.class))).thenReturn(claim);

        String result = claimService.reviewClaim(1L, "APPROVED");

        assertEquals("Claim APPROVED", result);
        assertEquals("APPROVED", claim.getStatus());
    }

    @Test
    void testReviewClaim_Reject() {
        when(claimRepository.findById(anyLong())).thenReturn(Optional.of(claim));
        when(claimRepository.save(any(Claim.class))).thenReturn(claim);

        String result = claimService.reviewClaim(1L, "REJECTED");

        assertEquals("Claim REJECTED", result);
        assertEquals("REJECTED", claim.getStatus());
    }

    @Test
    void testReviewClaim_InvalidStatus() {
        when(claimRepository.findById(anyLong())).thenReturn(Optional.of(claim));

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> claimService.reviewClaim(1L, "PENDING"));

        assertEquals("Invalid status. Use APPROVED or REJECTED", exception.getMessage());
    }

    @Test
    void testGetAllClaims() {
        when(claimRepository.findAll()).thenReturn(Arrays.asList(claim));

        List<Claim> claims = claimService.getAllClaims();

        assertEquals(1, claims.size());
    }

    @Test
    void testGetReportData() {
        Claim approvedClaim = new Claim();
        approvedClaim.setStatus("APPROVED");
        Claim rejectedClaim = new Claim();
        rejectedClaim.setStatus("REJECTED");
        Claim uploadedClaim = new Claim();
        uploadedClaim.setStatus("UPLOADED");

        when(claimRepository.count()).thenReturn(3L);
        when(claimRepository.findAll()).thenReturn(Arrays.asList(approvedClaim, rejectedClaim, uploadedClaim));

        Map<String, Long> report = claimService.getReportData();

        assertEquals(3L, report.get("TOTAL_CLAIMS"));
        assertEquals(1L, report.get("APPROVED"));
        assertEquals(1L, report.get("REJECTED"));
    }
}
