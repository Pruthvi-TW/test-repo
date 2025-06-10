package com.ekyc.service;

import com.ekyc.domain.EkycRequest;
import com.ekyc.domain.EkycStatus;
import com.ekyc.dto.EkycRequestDTO;
import com.ekyc.repository.EkycRequestRepository;
import com.ekyc.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class EkycServiceTest {

    @Autowired
    private EkycService ekycService;

    @Autowired
    private EkycRequestRepository ekycRequestRepository;

    private EkycRequestDTO validRequest;

    @BeforeEach
    public void setUp() {
        validRequest = createValidEkycRequest();
    }

    @Test
    public void testCreateEkycRequest_ValidInput_Success() {
        EkycRequest createdRequest = ekycService.createEkycRequest(validRequest);
        
        assertNotNull(createdRequest);
        assertEquals(EkycStatus.INITIATED, createdRequest.getStatus());
        assertNotNull(createdRequest.getReferenceNumber());
        
        Optional<EkycRequest> savedRequest = ekycRequestRepository.findByReferenceNumber(createdRequest.getReferenceNumber());
        assertTrue(savedRequest.isPresent());
    }

    @Test
    public void testCreateEkycRequest_InvalidInput_ThrowsValidationException() {
        EkycRequestDTO invalidRequest = new EkycRequestDTO();
        
        assertThrows(ValidationException.class, () -> {
            ekycService.createEkycRequest(invalidRequest);
        });
    }

    @Test
    public void testUpdateEkycRequestStatus_ValidTransition_Success() {
        EkycRequest initialRequest = ekycService.createEkycRequest(validRequest);
        
        EkycRequest updatedRequest = ekycService.updateEkycRequestStatus(
            initialRequest.getReferenceNumber(), 
            EkycStatus.VERIFIED
        );
        
        assertEquals(EkycStatus.VERIFIED, updatedRequest.getStatus());
    }

    @Test
    public void testGetEkycRequest_ExistingReferenceNumber_ReturnsRequest() {
        EkycRequest createdRequest = ekycService.createEkycRequest(validRequest);
        
        Optional<EkycRequest> retrievedRequest = ekycService.getEkycRequest(
            createdRequest.getReferenceNumber()
        );
        
        assertTrue(retrievedRequest.isPresent());
        assertEquals(createdRequest.getReferenceNumber(), retrievedRequest.get().getReferenceNumber());
    }

    private EkycRequestDTO createValidEkycRequest() {
        EkycRequestDTO dto = new EkycRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhoneNumber("+1234567890");
        dto.setDateOfBirth(LocalDateTime.now().minusYears(30));
        return dto;
    }
}