package com.ekyc.service;

import com.ekyc.dto.EkycRequestDto;
import com.ekyc.dto.EkycResponseDto;
import com.ekyc.entity.OtpRequest;
import com.ekyc.exception.EkycException;
import com.ekyc.repository.OtpRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OtpServiceTest {

    @Autowired
    private OtpService otpService;

    @Autowired
    private EkycService ekycService;

    @Autowired
    private OtpRequestRepository otpRequestRepository;

    private String referenceNumber;
    private String mobileNumber;

    @BeforeEach
    void setUp() {
        // Create a valid eKYC request to get a reference number
        EkycRequestDto requestDto = new EkycRequestDto();
        requestDto.setFullName("John Doe");
        requestDto.setEmail("john.doe@example.com");
        requestDto.setMobileNumber("9876543210");
        requestDto.setAadhaarNumber("123456789012");
        requestDto.setPanNumber("ABCDE1234F");
        requestDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        requestDto.setAddress("123 Main St, Bangalore, Karnataka, 560001");

        EkycResponseDto response = ekycService.createEkycRequest(requestDto);
        referenceNumber = response.getReferenceNumber();
        mobileNumber = requestDto.getMobileNumber();
    }

    @Test
    @DisplayName("Should generate and send OTP successfully")
    void testGenerateAndSendOtp_Success() {
        // When
        String otp = otpService.generateAndSendOtp(referenceNumber, mobileNumber);

        // Then
        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}")); // 6-digit number

        // Verify OTP is saved in repository
        Optional<OtpRequest> savedOtp = otpRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(savedOtp.isPresent());
        assertEquals(otp, savedOtp.get().getOtp());
        assertEquals(mobileNumber, savedOtp.get().getMobileNumber());
        assertNotNull(savedOtp.get().getCreatedAt());
        assertFalse(savedOtp.get().isVerified());
    }

    @Test
    @DisplayName("Should throw exception when generating OTP for non-existent reference")
    void testGenerateAndSendOtp_NonExistentReference() {
        // Given
        String nonExistentReference = "NON_EXISTENT_REF";

        // When & Then
        EkycException exception = assertThrows(EkycException.class, 
            () -> otpService.generateAndSendOtp(nonExistentReference, mobileNumber));
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should verify OTP successfully")
    void testVerifyOtp_Success() {
        // Given
        String otp = otpService.generateAndSendOtp(referenceNumber, mobileNumber);

        // When
        boolean result = otpService.verifyOtp(referenceNumber, otp);

        // Then
        assertTrue(result);

        // Verify OTP is marked as verified in repository
        Optional<OtpRequest> verifiedOtp = otpRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(verifiedOtp.isPresent());
        assertTrue(verifiedOtp.get().isVerified());
        assertNotNull(verifiedOtp.get().getVerifiedAt());
    }

    @Test
    @DisplayName("Should return false when verifying with incorrect OTP")
    void testVerifyOtp_IncorrectOtp() {
        // Given
        otpService.generateAndSendOtp(referenceNumber, mobileNumber);
        String incorrectOtp = "999999"; // Incorrect OTP

        // When
        boolean result = otpService.verifyOtp(referenceNumber, incorrectOtp);

        // Then
        assertFalse(result);

        // Verify OTP is not marked as verified in repository
        Optional<OtpRequest> otpRequest = otpRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(otpRequest.isPresent());
        assertFalse(otpRequest.get().isVerified());
        assertNull(otpRequest.get().getVerifiedAt());
    }

    @Test
    @DisplayName("Should throw exception when verifying OTP for non-existent reference")
    void testVerifyOtp_NonExistentReference() {
        // Given
        String nonExistentReference = "NON_EXISTENT_REF";
        String otp = "123456";

        // When & Then
        EkycException exception = assertThrows(EkycException.class, 
            () -> otpService.verifyOtp(nonExistentReference, otp));
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should throw exception when verifying expired OTP")
    void testVerifyOtp_ExpiredOtp() {
        // Given
        String otp = otpService.generateAndSendOtp(referenceNumber, mobileNumber);
        
        // Manually expire the OTP by setting creation time to past
        Optional<OtpRequest> otpRequest = otpRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(otpRequest.isPresent());
        OtpRequest request = otpRequest.get();
        request.setCreatedAt(LocalDateTime.now().minusMinutes(16)); // Beyond 15-minute expiry
        otpRequestRepository.save(request);

        // When & Then
        EkycException exception = assertThrows(EkycException.class, 
            () -> otpService.verifyOtp(referenceNumber, otp));
        
        assertTrue(exception.getMessage().contains("expired"));
    }

    @Test
    @DisplayName("Should regenerate OTP successfully")
    void testRegenerateOtp_Success() {
        // Given
        String firstOtp = otpService.generateAndSendOtp(referenceNumber, mobileNumber);

        // When
        String secondOtp = otpService.generateAndSendOtp(referenceNumber, mobileNumber);

        // Then
        assertNotNull(secondOtp);
        assertNotEquals(firstOtp, secondOtp); // New OTP should be different

        // Verify new OTP is saved in repository
        Optional<OtpRequest> savedOtp = otpRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(savedOtp.isPresent());
        assertEquals(secondOtp, savedOtp.get().getOtp());
        assertFalse(savedOtp.get().isVerified());
    }

    @Test
    @DisplayName("Should track OTP attempts")
    void testOtpAttempts() {
        // Given
        String otp = otpService.generateAndSendOtp(referenceNumber, mobileNumber);
        String incorrectOtp = "999999";

        // When - First attempt (incorrect)
        otpService.verifyOtp(referenceNumber, incorrectOtp);
        
        // Then
        Optional<OtpRequest> firstAttempt = otpRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(firstAttempt.isPresent());
        assertEquals(1, firstAttempt.get().getAttempts());
        
        // When - Second attempt (incorrect)
        otpService.verifyOtp(referenceNumber, incorrectOtp);
        
        // Then
        Optional<OtpRequest> secondAttempt = otpRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(secondAttempt.isPresent());
        assertEquals(2, secondAttempt.get().getAttempts());
        
        // When - Third attempt (correct)
        otpService.verifyOtp(referenceNumber, otp);
        
        // Then
        Optional<OtpRequest> thirdAttempt = otpRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(thirdAttempt.isPresent());
        assertEquals(3, thirdAttempt.get().getAttempts());
        assertTrue(thirdAttempt.get().isVerified());
    }

    @Test
    @DisplayName("Should block after max attempts")
    void testMaxOtpAttempts() {
        // Given
        String otp = otpService.generateAndSendOtp(referenceNumber, mobileNumber);
        String incorrectOtp = "999999";
        
        // When - Make 5 incorrect attempts
        for (int i = 0; i < 5; i++) {
            otpService.verifyOtp(referenceNumber, incorrectOtp);
        }
        
        // Then - 6th attempt should be blocked
        EkycException exception = assertThrows(EkycException.class, 
            () -> otpService.verifyOtp(referenceNumber, otp));
        
        assertTrue(exception.getMessage().contains("maximum attempts"));
        
        // Verify OTP is blocked in repository
        Optional<OtpRequest> blockedOtp = otpRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(blockedOtp.isPresent());
        assertEquals(5, blockedOtp.get().getAttempts());
        assertTrue(blockedOtp.get().isBlocked());
    }

    @ParameterizedTest
    @ValueSource(strings = {"9876543210", "8765432109", "7654321098"})
    @DisplayName("Should generate OTP for different mobile numbers")
    void testGenerateOtp_DifferentMobileNumbers(String mobile) {
        // When
        String otp = otpService.generateAndSendOtp(referenceNumber, mobile);

        // Then
        assertNotNull(otp);
        assertEquals(6, otp.length());
        
        // Verify OTP is saved with correct mobile number
        Optional<OtpRequest> savedOtp = otpRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(savedOtp.isPresent());
        assertEquals(mobile, savedOtp.get().getMobileNumber());
    }

    @Test
    @DisplayName("Should check if OTP is verified")
    void testIsOtpVerified() {
        // Given
        String otp = otpService.generateAndSendOtp(referenceNumber, mobileNumber);
        
        // When - Before verification
        boolean beforeVerification = otpService.isOtpVerified(referenceNumber);
        
        // Then
        assertFalse(beforeVerification);
        
        // When - After verification
        otpService.verifyOtp(referenceNumber, otp);
        boolean afterVerification = otpService.isOtpVerified(referenceNumber);
        
        // Then
        assertTrue(afterVerification);
    }

    @Test
    @DisplayName("Should throw exception when checking verification status for non-existent reference")
    void testIsOtpVerified_NonExistentReference() {
        // Given
        String nonExistentReference = "NON_EXISTENT_REF";

        // When & Then
        EkycException exception = assertThrows(EkycException.class, 
            () -> otpService.isOtpVerified(nonExistentReference));
        
        assertTrue(exception.getMessage().contains("not found"));
    }
}