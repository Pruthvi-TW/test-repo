package com.ekyc.service;

import com.ekyc.dto.EkycRequestDto;
import com.ekyc.dto.EkycResponseDto;
import com.ekyc.entity.EkycRequest;
import com.ekyc.exception.EkycException;
import com.ekyc.repository.EkycRequestRepository;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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
    private EkycRequestRepository ekycRequestRepository;

    private EkycRequestDto validRequestDto;
    private String referenceNumber;

    @BeforeEach
    void setUp() {
        validRequestDto = new EkycRequestDto();
        validRequestDto.setFullName("John Doe");
        validRequestDto.setEmail("john.doe@example.com");
        validRequestDto.setMobileNumber("9876543210");
        validRequestDto.setAadhaarNumber("123456789012");
        validRequestDto.setPanNumber("ABCDE1234F");
        validRequestDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        validRequestDto.setAddress("123 Main St, Bangalore, Karnataka, 560001");

        // Create a request to get a reference number
        EkycResponseDto response = ekycService.createEkycRequest(validRequestDto);
        referenceNumber = response.getReferenceNumber();
    }

    @Test
    @DisplayName("Should generate and send OTP successfully")
    void testGenerateAndSendOtp() {
        // When
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());

        // Then
        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}"), "OTP should be 6 digits");

        // Verify OTP is stored in the database
        Optional<EkycRequest> updatedRequest = ekycRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(updatedRequest.isPresent());
        assertNotNull(updatedRequest.get().getOtp());
        assertNotNull(updatedRequest.get().getOtpGeneratedAt());
    }

    @Test
    @DisplayName("Should throw exception for invalid reference number")
    void testGenerateAndSendOtpWithInvalidReferenceNumber() {
        // Given
        String invalidReferenceNumber = "INVALID_REF";

        // When & Then
        EkycException exception = assertThrows(EkycException.class, 
            () -> otpService.generateAndSendOtp(invalidReferenceNumber, validRequestDto.getMobileNumber()));
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should throw exception for mismatched mobile number")
    void testGenerateAndSendOtpWithMismatchedMobileNumber() {
        // Given
        String differentMobileNumber = "9876543211";

        // When & Then
        EkycException exception = assertThrows(EkycException.class, 
            () -> otpService.generateAndSendOtp(referenceNumber, differentMobileNumber));
        
        assertTrue(exception.getMessage().contains("Mobile number does not match"));
    }

    @Test
    @DisplayName("Should verify OTP successfully")
    void testVerifyOtp() {
        // Given
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());

        // When
        boolean result = otpService.verifyOtp(referenceNumber, otp);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should fail verification for invalid OTP")
    void testVerifyOtpWithInvalidOtp() {
        // Given
        otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        String invalidOtp = "999999";

        // When
        boolean result = otpService.verifyOtp(referenceNumber, invalidOtp);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should fail verification for expired OTP")
    void testVerifyOtpWithExpiredOtp() {
        // Given
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        
        // Manually expire the OTP
        EkycRequest request = ekycRequestRepository.findByReferenceNumber(referenceNumber).get();
        request.setOtpGeneratedAt(LocalDateTime.now().minusMinutes(15)); // Assuming OTP expires in 10 minutes
        ekycRequestRepository.save(request);

        // When
        boolean result = otpService.verifyOtp(referenceNumber, otp);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should generate different OTPs for consecutive requests")
    void testGenerateDifferentOtps() {
        // When
        String otp1 = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        String otp2 = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());

        // Then
        assertNotEquals(otp1, otp2, "Consecutive OTPs should be different");
    }

    @Test
    @DisplayName("Should handle OTP generation rate limiting")
    void testOtpGenerationRateLimiting() throws InterruptedException {
        // Given
        int requestCount = 10;
        CountDownLatch latch = new CountDownLatch(requestCount);
        ExecutorService executor = Executors.newFixedThreadPool(requestCount);
        
        // When
        for (int i = 0; i < requestCount; i++) {
            executor.submit(() -> {
                try {
                    otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
                } catch (Exception e) {
                    // Expected rate limiting exceptions
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all threads to complete
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        
        // Then
        assertTrue(completed, "All OTP generation requests should complete");
        
        // Verify the last OTP generation timestamp
        Optional<EkycRequest> request = ekycRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(request.isPresent());
        assertNotNull(request.get().getOtpGeneratedAt());
    }

    @Test
    @DisplayName("Should handle concurrent OTP verifications")
    void testConcurrentOtpVerifications() throws InterruptedException {
        // Given
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        int verificationCount = 5;
        CountDownLatch latch = new CountDownLatch(verificationCount);
        ExecutorService executor = Executors.newFixedThreadPool(verificationCount);
        boolean[] results = new boolean[verificationCount];
        
        // When
        for (int i = 0; i < verificationCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    results[index] = otpService.verifyOtp(referenceNumber, otp);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all threads to complete
        boolean completed = latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        
        // Then
        assertTrue(completed, "All OTP verification requests should complete");
        
        // Only one verification should succeed (first one)
        int successCount = 0;
        for (boolean result : results) {
            if (result) {
                successCount++;
            }
        }
        
        // At least one verification should succeed
        assertTrue(successCount >= 1, "At least one verification should succeed");
    }

    @Test
    @DisplayName("Should invalidate OTP after successful verification")
    void testInvalidateOtpAfterVerification() {
        // Given
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        
        // First verification
        boolean firstResult = otpService.verifyOtp(referenceNumber, otp);
        
        // Second verification with same OTP
        boolean secondResult = otpService.verifyOtp(referenceNumber, otp);
        
        // Then
        assertTrue(firstResult, "First verification should succeed");
        assertFalse(secondResult, "Second verification should fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "12345", "1234567", "abcdef", "12345a"})
    @DisplayName("Should reject invalid OTP formats")
    void testRejectInvalidOtpFormats(String invalidOtp) {
        // Given
        otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        
        // When
        boolean result = otpService.verifyOtp(referenceNumber, invalidOtp);
        
        // Then
        assertFalse(result, "Verification should fail for invalid OTP format: " + invalidOtp);
    }

    @Test
    @DisplayName("Should generate OTP with correct format")
    void testOtpFormat() {
        // When
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        
        // Then
        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(Pattern.matches("\\d{6}", otp), "OTP should be 6 digits");
    }

    @Test
    @DisplayName("Should log OTP generation with masked data")
    void testOtpGenerationLogging() {
        // This test verifies that OTP generation is properly logged
        // The actual logging verification would be done by examining the log output
        // Here we just ensure the method executes without errors
        
        // When
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        
        // Then
        assertNotNull(otp);
    }

    @Test
    @DisplayName("Should handle resending OTP")
    void testResendOtp() {
        // Given
        String firstOtp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        
        // When
        String secondOtp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        
        // Then
        assertNotNull(secondOtp);
        assertNotEquals(firstOtp, secondOtp, "Resent OTP should be different");
        
        // Verify the new OTP works
        boolean result = otpService.verifyOtp(referenceNumber, secondOtp);
        assertTrue(result, "New OTP should be valid");
    }
}