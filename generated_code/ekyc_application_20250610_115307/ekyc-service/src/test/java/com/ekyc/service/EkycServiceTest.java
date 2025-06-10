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
        // Arrange
        EkycRequestDTO result = ekycService.createEkycRequest(validRequest);

        // Assert
        assertNotNull(result.getReferenceNumber());
        assertEquals(EkycStatus.INITIATED, result.getStatus());
        assertTrue(ValidationUtil.isValidReferenceNumber(result.getReferenceNumber()));
    }

    @Test
    public void testCreateEkycRequest_InvalidInput_ThrowsException() {
        // Arrange
        EkycRequestDTO invalidRequest = new EkycRequestDTO();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            ekycService.createEkycRequest(invalidRequest);
        });
    }

    @Test
    public void testGetEkycRequest_ExistingRequest_ReturnsRequest() {
        // Arrange
        EkycRequestDTO createdRequest = ekycService.createEkycRequest(validRequest);
        String referenceNumber = createdRequest.getReferenceNumber();

        // Act
        Optional<EkycRequestDTO> retrievedRequest = ekycService.getEkycRequest(referenceNumber);

        // Assert
        assertTrue(retrievedRequest.isPresent());
        assertEquals(referenceNumber, retrievedRequest.get().getReferenceNumber());
    }

    @Test
    public void testUpdateEkycRequestStatus_ValidTransition_Success() {
        // Arrange
        EkycRequestDTO createdRequest = ekycService.createEkycRequest(validRequest);
        String referenceNumber = createdRequest.getReferenceNumber();

        // Act
        EkycRequestDTO updatedRequest = ekycService.updateEkycRequestStatus(
            referenceNumber, 
            EkycStatus.DOCUMENT_VERIFICATION_PENDING
        );

        // Assert
        assertEquals(EkycStatus.DOCUMENT_VERIFICATION_PENDING, updatedRequest.getStatus());
    }

    @Test
    public void testCreateEkycRequest_DuplicateRequest_ThrowsException() {
        // Arrange
        ekycService.createEkycRequest(validRequest);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            ekycService.createEkycRequest(validRequest);
        });
    }

    private EkycRequestDTO createValidEkycRequest() {
        EkycRequestDTO request = new EkycRequestDTO();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPhoneNumber("+1234567890");
        request.setDateOfBirth(LocalDateTime.now().minusYears(30));
        return request;
    }
}