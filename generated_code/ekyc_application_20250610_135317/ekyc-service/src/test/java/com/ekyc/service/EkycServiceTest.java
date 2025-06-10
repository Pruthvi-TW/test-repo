package com.ekyc.service;

import com.ekyc.dto.EkycRequestDto;
import com.ekyc.entity.EkycRequest;
import com.ekyc.repository.EkycRequestRepository;
import com.ekyc.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class EkycServiceTest {

    @Autowired
    private EkycService ekycService;

    @Autowired
    private EkycRequestRepository ekycRequestRepository;

    @Autowired
    private ValidationUtil validationUtil;

    private EkycRequestDto validRequest;

    @BeforeEach
    public void setup() {
        validRequest = createValidEkycRequest();
    }

    @Test
    @DisplayName("Successful eKYC Request Processing")
    public void testSuccessfulEkycRequest() {
        // Act
        String referenceNumber = ekycService.initiateEkycProcess(validRequest);

        // Assert
        assertNotNull(referenceNumber);
        assertTrue(referenceNumber.startsWith("EKYC-"));

        // Verify repository save
        EkycRequest savedRequest = ekycRequestRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new AssertionError("Request not saved"));
        
        assertEquals(validRequest.getFirstName(), savedRequest.getFirstName());
    }

    @Test
    @DisplayName("Validate Input Validation")
    public void testInputValidation() {
        // Arrange
        EkycRequestDto invalidRequest = new EkycRequestDto();
        invalidRequest.setFirstName("");  // Invalid input

        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> validationUtil.validateEkycRequest(invalidRequest),
            "Invalid request should throw validation exception"
        );
    }

    @Test
    @DisplayName("Duplicate Request Prevention")
    public void testDuplicateRequestPrevention() {
        // Act
        String firstReferenceNumber = ekycService.initiateEkycProcess(validRequest);
        
        // Assert
        assertThrows(DuplicateRequestException.class, 
            () -> ekycService.initiateEkycProcess(validRequest),
            "Duplicate request should be prevented"
        );
    }

    private EkycRequestDto createValidEkycRequest() {
        EkycRequestDto dto = new EkycRequestDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhoneNumber("+911234567890");
        dto.setIdType("PASSPORT");
        dto.setIdNumber("A1234567");
        return dto;
    }
}