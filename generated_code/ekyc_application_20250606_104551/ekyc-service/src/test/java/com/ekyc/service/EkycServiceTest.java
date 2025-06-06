```java
package com.ekyc.service;

import com.ekyc.domain.EkycRequest;
import com.ekyc.domain.EkycStatus;
import com.ekyc.domain.VerificationType;
import com.ekyc.repository.EkycRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EkycServiceTest {

    @Autowired
    private EkycService ekycService;

    @Autowired
    private EkycRequestRepository ekycRequestRepository;

    private EkycRequest testRequest;

    @BeforeEach
    void setUp() {
        testRequest = new EkycRequest();
        testRequest.setReferenceNumber(UUID.randomUUID().toString());
        testRequest.setCustomerId("TEST-CUST-001");
        testRequest.setVerificationType(VerificationType.AADHAAR);
        testRequest.setStatus(EkycStatus.INITIATED);
        testRequest.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void initiateVerification_Success() {
        EkycRequest result = ekycService.initiateVerification(testRequest);
        
        assertNotNull(result);
        assertNotNull(result.getReferenceNumber());
        assertEquals(EkycStatus.INITIATED, result.getStatus());
        assertTrue(ekycRequestRepository.findById(result.getId()).isPresent());
    }

    @Test
    void initiateVerification_WithInvalidData_ThrowsException() {
        testRequest.setCustomerId(null);
        
        assertThrows(IllegalArgumentException.class, () -> 
            ekycService.initiateVerification(testRequest));
    }

    @Test
    void verifyDocument_Success() {
        EkycRequest initiated = ekycService.initiateVerification(testRequest);
        
        EkycRequest verified = ekycService.verifyDocument(
            initiated.getReferenceNumber(), 
            "test-document-data"
        );
        
        assertEquals(EkycStatus.VERIFIED, verified.getStatus());
        assertNotNull(verified.getCompletedAt());
    }

    @Test
    void getVerificationStatus_ReturnsCorrectStatus() {
        EkycRequest saved = ekycService.initiateVerification(testRequest);
        
        EkycStatus status = ekycService.getVerificationStatus(
            saved.getReferenceNumber()
        );
        
        assertEquals(EkycStatus.INITIATED, status);
    }

    @Test
    void retentionPolicy_DeletesExpiredRecords() {
        testRequest.setCreatedAt(LocalDateTime.now().minusDays(91));
        EkycRequest expired = ekycRequestRepository.save(testRequest);
        
        ekycService.applyRetentionPolicy();
        
        assertTrue(ekycRequestRepository.findById(expired.getId()).isEmpty());
    }
}
```