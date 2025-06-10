package com.ekyc.integration;

import com.ekyc.EkycApplication;
import com.ekyc.dto.EkycRequestDto;
import com.ekyc.service.EkycService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = EkycApplication.class)
@ActiveProfiles("test")
public class EkycIntegrationTest {

    @Autowired
    private EkycService ekycService;

    @Test
    @DisplayName("Complete eKYC Flow Integration Test")
    public void testCompleteEkycFlow() {
        // Arrange
        EkycRequestDto requestDto = createValidEkycRequest();

        // Act
        String referenceNumber = ekycService.initiateEkycProcess(requestDto);

        // Assert
        assertNotNull(referenceNumber, "Reference number should be generated");
        assertTrue(referenceNumber.matches("EKYC-\\d{10}"), "Reference number format invalid");
    }

    @Test
    @DisplayName("Invalid eKYC Request Handling")
    public void testInvalidEkycRequest() {
        // Arrange
        EkycRequestDto invalidRequest = createInvalidEkycRequest();

        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> ekycService.initiateEkycProcess(invalidRequest),
            "Invalid request should throw validation exception"
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

    private EkycRequestDto createInvalidEkycRequest() {
        EkycRequestDto dto = new EkycRequestDto();
        dto.setFirstName("");  // Invalid input
        return dto;
    }
}