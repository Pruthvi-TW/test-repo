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
        validRequest.setCustomerId("CUS123456");
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
        EkycRequest savedRequest = ekycService.createRequest(validRequest);
        
        assertNotNull(savedRequest.getId());
        assertNotNull(savedRequest.getReferenceNumber());
        assertEquals(EkycStatus.INITIATED, savedRequest.getStatus());
        
        Optional<EkycRequest> foundRequest = ekycRequestRepository.findById(savedRequest.getId());
        assertTrue(foundRequest.isPresent());
    }

    @Test
    void whenInvalidEmail_thenThrowValidationException() {
        validRequest.setEmailAddress("invalid-email");
        
        assertThrows(EkycValidationException.class, () -> {
            ekycService.createRequest(validRequest);
        });
    }

    @Test
    void whenDuplicateRequest_thenThrowException() {
        EkycRequest firstRequest = ekycService.createRequest(validRequest);
        
        assertThrows(EkycValidationException.class, () -> {
            ekycService.createRequest(validRequest);
        });
    }

    @Test
    void whenRequestCompleted_thenUpdateStatus() {
        EkycRequest savedRequest = ekycService.createRequest(validRequest);
        String referenceNumber = savedRequest.getReferenceNumber();
        
        ekycService.updateStatus(referenceNumber, EkycStatus.COMPLETED);
        
        EkycRequest updatedRequest = ekycService.findByReferenceNumber(referenceNumber);
        assertEquals(EkycStatus.COMPLETED, updatedRequest.getStatus());
    }

    @Test
    void testRetentionPolicy() {
        validRequest.setCreatedAt(LocalDateTime.now().minusDays(91));
        EkycRequest savedRequest = ekycService.createRequest(validRequest);
        
        ekycService.applyRetentionPolicy();
        
        Optional<EkycRequest> shouldBeDeleted = ekycRequestRepository
            .findById(savedRequest.getId());
        assertFalse(shouldBeDeleted.isPresent());
    }
}
```