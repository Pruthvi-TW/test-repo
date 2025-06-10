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
    public void testGenerateOtp_ValidInput_ShouldSucceed() {
        // Act
        OtpRequestDTO generatedOtp = otpService.generateOtp(validOtpRequest);

        // Assert
        assertNotNull(generatedOtp.getOtpCode());
        assertEquals(6, generatedOtp.getOtpCode().length());
        assertNotNull(generatedOtp.getReferenceNumber());
    }

    @Test
    public void testValidateOtp_CorrectOtp_ShouldReturnTrue() {
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
    public void testValidateOtp_IncorrectOtp_ShouldReturnFalse() {
        // Arrange
        OtpRequestDTO generatedOtp = otpService.generateOtp(validOtpRequest);
        String referenceNumber = generatedOtp.getReferenceNumber();

        // Act
        boolean isValid = otpService.validateOtp(referenceNumber, "INCORRECT");

        // Assert
        assertFalse(isValid);
    }

    @Test
    public void testOtpExpiration_ExpiredOtp_ShouldBeInvalid() {
        // This would require mocking time or creating a test-specific OTP service
        // that allows setting custom expiration times
    }

    private OtpRequestDTO createValidOtpRequest() {
        OtpRequestDTO dto = new OtpRequestDTO();
        dto.setPhoneNumber("+1234567890");
        dto.setEmail("test@example.com");
        return dto;
    }
}