package com.ekyc.service;

import com.ekyc.domain.OtpRequest;
import com.ekyc.dto.OtpRequestDTO;
import com.ekyc.repository.OtpRequestRepository;
import com.ekyc.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

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
        assertNotNull(result.getOtpReference());
        assertTrue(ValidationUtil.isValidOtp(result.getOtp()));
        assertNotNull(result.getExpiryTime());
    }

    @Test
    public void testValidateOtp_CorrectOtp_Success() {
        // Arrange
        OtpRequestDTO generatedOtp = otpService.generateOtp(validOtpRequest);

        // Act
        boolean isValid = otpService.validateOtp(
            generatedOtp.getOtpReference(), 
            generatedOtp.getOtp()
        );

        // Assert
        assertTrue(isValid);
    }

    @Test
    public void testValidateOtp_IncorrectOtp_Failure() {
        // Arrange
        OtpRequestDTO generatedOtp = otpService.generateOtp(validOtpRequest);

        // Act
        boolean isValid = otpService.validateOtp(
            generatedOtp.getOtpReference(), 
            "INCORRECT_OTP"
        );

        // Assert
        assertFalse(isValid);
    }

    @Test
    public void testValidateOtp_ExpiredOtp_Failure() {
        // Arrange
        OtpRequestDTO generatedOtp = otpService.generateOtp(validOtpRequest);
        
        // Simulate OTP expiry by setting expiry time in the past
        OtpRequest otpRequest = otpRequestRepository
            .findByOtpReference(generatedOtp.getOtpReference())
            .orElseThrow();
        otpRequest.setExpiryTime(LocalDateTime.now().minusMinutes(10));
        otpRequestRepository.save(otpRequest);

        // Act
        boolean isValid = otpService.validateOtp(
            generatedOtp.getOtpReference(), 
            generatedOtp.getOtp()
        );

        // Assert
        assertFalse(isValid);
    }

    private OtpRequestDTO createValidOtpRequest() {
        OtpRequestDTO request = new OtpRequestDTO();
        request.setPhoneNumber("+1234567890");
        request.setEmail("john.doe@example.com");
        return request;
    }
}