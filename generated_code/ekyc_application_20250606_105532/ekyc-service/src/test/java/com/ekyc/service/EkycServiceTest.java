```java
package com.ekyc.service;

import com.ekyc.domain.EkycRequest;
import com.ekyc.domain.EkycStatus;
import com.ekyc.domain.VerificationType;
import com.ekyc.exception.EkycValidationException;
import com.ekyc.repository.EkycRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EkycServiceTest {

    @Autowired
    private EkycService ekycService;

    @Autowired
    private EkycRequestRepository ekycRequestRepository;

    private EkycRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new EkycRequest();
        validRequest.setCustomerId("TEST123");
        validRequest.setDocumentNumber("PASS123456");
        validRequest.setVerificationType(VerificationType.PASSPORT);
        validRequest.setStatus(EkycStatus.INITIATED);
        validRequest.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void whenValidRequest_thenCreateSuccess() {
        EkycRequest saved = ekycService.createRequest(validRequest);
        
        assertNotNull(saved.getId());
        assertNotNull(saved.getReferenceNumber());
        assertEquals(EkycStatus.INITIATED, saved.getStatus());
        
        Optional<EkycRequest> found = ekycRequestRepository.findById(saved.getId());
        assertTrue(found.isPresent());
    }

    @Test
    void whenInvalidDocument_thenThrowException() {
        validRequest.setDocumentNumber("123"); // Invalid format

        assertThrows(EkycValidationException.class, () -> {
            ekycService.createRequest(validRequest);
        });
    }

    @Test
    void whenProcessValid_thenUpdateStatus() {
        EkycRequest saved = ekycService.createRequest(validRequest);
        EkycRequest processed = ekycService.processRequest(saved.getReferenceNumber());
        
        assertEquals(EkycStatus.COMPLETED, processed.getStatus());
        assertNotNull(processed.getCompletedAt());
    }

    @Test
    void whenRetentionPeriodExpired_thenDataMasked() {
        validRequest.setCreatedAt(LocalDateTime.now().minusDays(90));
        EkycRequest saved = ekycService.createRequest(validRequest);
        
        EkycRequest retrieved = ekycService.getRequest(saved.getReferenceNumber());
        assertTrue(retrieved.getDocumentNumber().startsWith("MASKED-"));
    }

    @Test
    void testAuditLogging() {
        EkycRequest saved = ekycService.createRequest(validRequest);
        
        // Verify logs contain masked PII
        // This would require a custom test appender to verify log contents
        // Implementation depends on logging framework used
    }
}
```