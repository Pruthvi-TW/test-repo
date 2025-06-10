package com.ekyc.service;

import com.ekyc.domain.OtpRequest;
import com.ekyc.dto.OtpRequestDTO;
import com.ekyc.repository.OtpRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class OtpServiceTest {

    @Autowired
    private OtpService otpService;

    @Autowired
    private OtpRequestRepository otpRequestRepository;

    private OtpRequestDTO validOtpRequest;

    @BeforeEach
    public void setUp() {
        validOtpRequest = createValidOtpRequest();
    }

    @Test
    public void testGenerateOtp_ValidInput_Success() {
        // Act
        OtpRequestDTO result = otpService.generateOtp(validOtpRequest);

        // Assert
        assertNotNull(result.getOtpCode());
        assertTrue(result.getOtpCode().length() == 6);
        assertNotNull(result.getReferenceNumber());
    }

    @Test
    public void testValidateOtp_CorrectOtp_ReturnsTrue() {
        // Arrange
        OtpRequestDTO generatedOtp = otpService.generateOtp(validOtpRequest);
        String otpCode = generatedOtp.getOtpCode();
        String referenceNumber = generatedOtp.getReferenceNumber();

        // Act
        boolean isValid = otpService.validateOtp(referenceNumber, otpCode);

        // Assert
        assertTrue(isValid);
    }

    @Test
    public void testValidateOtp_IncorrectOtp_ReturnsFalse() {
        // Arrange
        OtpRequestDTO generatedOtp = otpService.generateOtp(validOtpRequest);
        String referenceNumber = generatedOtp.getReferenceNumber();

        // Act
        boolean isValid = otpService.validateOtp(referenceNumber, "INCORRECT");

        // Assert
        assertFalse(isValid);
    }

    @Test
    public void testOtpExpiration_ExpiredOtp_ReturnsFalse() {
        // This test would require mocking time or having a configurable OTP expiration
        // Implement based on your specific OTP expiration logic
    }

    private OtpRequestDTO createValidOtpRequest() {
        OtpRequestDTO request = new OtpRequestDTO();
        request.setPhoneNumber("+1234567890");
        request.setEmail("john.doe@example.com");
        return request;
    }
}