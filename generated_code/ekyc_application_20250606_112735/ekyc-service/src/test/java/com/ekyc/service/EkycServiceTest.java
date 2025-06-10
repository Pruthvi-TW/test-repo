```java
package com.ekyc.service;

import com.ekyc.domain.EkycRequest;
import com.ekyc.domain.EkycStatus;
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
        validRequest.setCustomerId("CUS123");
        validRequest.setFullName("John Doe");
        validRequest.setDateOfBirth("1990-01-01");
        validRequest.setEmailAddress("john.doe@example.com");
        validRequest.setPhoneNumber("+1234567890");
        validRequest.setDocumentType("PASSPORT");
        validRequest.setDocumentNumber("P123456789");
        validRequest.setStatus(EkycStatus.INITIATED);
        validRequest.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void whenValidRequest_thenCreateSuccessfully() {
        EkycRequest saved = ekycService.createRequest(validRequest);
        
        assertNotNull(saved.getId());
        assertNotNull(saved.getReferenceNumber());
        assertEquals(EkycStatus.INITIATED, saved.getStatus());
        
        Optional<EkycRequest> found = ekycRequestRepository.findById(saved.getId());
        assertTrue(found.isPresent());
    }

    @Test
    void whenInvalidEmail_thenThrowValidationException() {
        validRequest.setEmailAddress("invalid-email");
        
        assertThrows(EkycValidationException.class, () -> {
            ekycService.createRequest(validRequest);
        });
    }

    @Test
    void whenRequestProcessed_thenUpdateStatus() {
        EkycRequest saved = ekycService.createRequest(validRequest);
        
        ekycService.updateStatus(saved.getReferenceNumber(), EkycStatus.VERIFIED);
        
        Optional<EkycRequest> updated = ekycRequestRepository.findByReferenceNumber(saved.getReferenceNumber());
        assertTrue(updated.isPresent());
        assertEquals(EkycStatus.VERIFIED, updated.get().getStatus());
    }

    @Test
    void whenRequestExpired_thenAutoArchive() {
        validRequest.setCreatedAt(LocalDateTime.now().minusDays(91)); // Past retention period
        EkycRequest saved = ekycService.createRequest(validRequest);
        
        ekycService.archiveExpiredRequests();
        
        Optional<EkycRequest> archived = ekycRequestRepository.findById(saved.getId());
        assertTrue(archived.isPresent());
        assertEquals(EkycStatus.ARCHIVED, archived.get().getStatus());
    }
}
```