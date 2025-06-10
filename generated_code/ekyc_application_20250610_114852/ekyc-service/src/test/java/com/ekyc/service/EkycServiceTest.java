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

    private EkycRequestDTO validRequestDTO;

    @BeforeEach
    public void setUp() {
        validRequestDTO = createValidEkycRequestDTO();
    }

    @Test
    public void testCreateEkycRequest_ValidInput_ShouldSucceed() {
        // Arrange
        EkycRequestDTO result = ekycService.createEkycRequest(validRequestDTO);

        // Assert
        assertNotNull(result.getReferenceNumber());
        assertEquals(EkycStatus.INITIATED, result.getStatus());
        assertTrue(ValidationUtil.isValidReferenceNumber(result.getReferenceNumber()));
    }

    @Test
    public void testCreateEkycRequest_InvalidInput_ShouldThrowException() {
        // Arrange
        EkycRequestDTO invalidDTO = new EkycRequestDTO();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            ekycService.createEkycRequest(invalidDTO);
        });
    }

    @Test
    public void testGetEkycRequest_ExistingRequest_ShouldReturnRequest() {
        // Arrange
        EkycRequestDTO createdRequest = ekycService.createEkycRequest(validRequestDTO);
        String referenceNumber = createdRequest.getReferenceNumber();

        // Act
        Optional<EkycRequestDTO> retrievedRequest = ekycService.getEkycRequest(referenceNumber);

        // Assert
        assertTrue(retrievedRequest.isPresent());
        assertEquals(referenceNumber, retrievedRequest.get().getReferenceNumber());
    }

    @Test
    public void testUpdateEkycRequestStatus_ValidTransition_ShouldSucceed() {
        // Arrange
        EkycRequestDTO createdRequest = ekycService.createEkycRequest(validRequestDTO);
        String referenceNumber = createdRequest.getReferenceNumber();

        // Act
        EkycRequestDTO updatedRequest = ekycService.updateEkycRequestStatus(
            referenceNumber, 
            EkycStatus.DOCUMENT_VERIFICATION
        );

        // Assert
        assertEquals(EkycStatus.DOCUMENT_VERIFICATION, updatedRequest.getStatus());
    }

    private EkycRequestDTO createValidEkycRequestDTO() {
        EkycRequestDTO dto = new EkycRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhoneNumber("+1234567890");
        dto.setDateOfBirth(LocalDateTime.now().minusYears(25));
        return dto;
    }
}