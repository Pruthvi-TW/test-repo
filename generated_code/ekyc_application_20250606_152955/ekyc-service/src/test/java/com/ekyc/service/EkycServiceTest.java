package com.ekyc.service;

import com.ekyc.dto.EkycRequestDto;
import com.ekyc.dto.EkycResponseDto;
import com.ekyc.entity.EkycRequest;
import com.ekyc.entity.EkycStatus;
import com.ekyc.exception.EkycException;
import com.ekyc.exception.ValidationException;
import com.ekyc.repository.EkycRequestRepository;
import com.ekyc.util.MaskingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class EkycServiceTest {

    @Autowired
    private EkycService ekycService;

    @Autowired
    private EkycRequestRepository ekycRequestRepository;

    @Autowired
    private OtpService otpService;

    private EkycRequestDto validRequestDto;

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
    }

    @Test
    @DisplayName("Should create eKYC request successfully")
    void testCreateEkycRequest() {
        // When
        EkycResponseDto response = ekycService.createEkycRequest(validRequestDto);

        // Then
        assertNotNull(response);
        assertNotNull(response.getReferenceNumber());
        assertEquals(EkycStatus.INITIATED, response.getStatus());

        // Verify saved in repository
        Optional<EkycRequest> savedRequest = ekycRequestRepository.findByReferenceNumber(response.getReferenceNumber());
        assertTrue(savedRequest.isPresent());
        assertEquals(validRequestDto.getFullName(), savedRequest.get().getFullName());
        assertEquals(validRequestDto.getEmail(), savedRequest.get().getEmail());
        assertEquals(validRequestDto.getMobileNumber(), savedRequest.get().getMobileNumber());
        assertEquals(validRequestDto.getAadhaarNumber(), savedRequest.get().getAadhaarNumber());
        assertEquals(validRequestDto.getPanNumber(), savedRequest.get().getPanNumber());
        assertEquals(validRequestDto.getDateOfBirth(), savedRequest.get().getDateOfBirth());
        assertEquals(validRequestDto.getAddress(), savedRequest.get().getAddress());
    }

    @Test
    @DisplayName("Should throw ValidationException for invalid Aadhaar number")
    void testCreateEkycRequestWithInvalidAadhaar() {
        // Given
        validRequestDto.setAadhaarNumber("12345"); // Invalid Aadhaar

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> ekycService.createEkycRequest(validRequestDto));
        
        assertTrue(exception.getMessage().contains("Aadhaar number"));
    }

    @Test
    @DisplayName("Should throw ValidationException for invalid PAN number")
    void testCreateEkycRequestWithInvalidPan() {
        // Given
        validRequestDto.setPanNumber("INVALID"); // Invalid PAN

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> ekycService.createEkycRequest(validRequestDto));
        
        assertTrue(exception.getMessage().contains("PAN number"));
    }

    @Test
    @DisplayName("Should throw ValidationException for invalid mobile number")
    void testCreateEkycRequestWithInvalidMobile() {
        // Given
        validRequestDto.setMobileNumber("123"); // Invalid mobile

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> ekycService.createEkycRequest(validRequestDto));
        
        assertTrue(exception.getMessage().contains("Mobile number"));
    }

    @Test
    @DisplayName("Should throw ValidationException for invalid email")
    void testCreateEkycRequestWithInvalidEmail() {
        // Given
        validRequestDto.setEmail("invalid-email"); // Invalid email

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> ekycService.createEkycRequest(validRequestDto));
        
        assertTrue(exception.getMessage().contains("Email"));
    }

    @Test
    @DisplayName("Should throw ValidationException for future date of birth")
    void testCreateEkycRequestWithFutureDob() {
        // Given
        validRequestDto.setDateOfBirth(LocalDate.now().plusDays(1)); // Future date

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> ekycService.createEkycRequest(validRequestDto));
        
        assertTrue(exception.getMessage().contains("Date of birth"));
    }

    @Test
    @DisplayName("Should verify OTP successfully")
    void testVerifyOtp() {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        // Generate OTP for testing
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());

        // When
        EkycResponseDto response = ekycService.verifyOtp(referenceNumber, otp);

        // Then
        assertNotNull(response);
        assertEquals(referenceNumber, response.getReferenceNumber());
        assertEquals(EkycStatus.OTP_VERIFIED, response.getStatus());

        // Verify updated in repository
        Optional<EkycRequest> updatedRequest = ekycRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(updatedRequest.isPresent());
        assertEquals(EkycStatus.OTP_VERIFIED, updatedRequest.get().getStatus());
    }

    @Test
    @DisplayName("Should throw exception for invalid OTP")
    void testVerifyOtpWithInvalidOtp() {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        // Generate OTP but use a different one
        otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        String invalidOtp = "999999"; // Invalid OTP

        // When & Then
        EkycException exception = assertThrows(EkycException.class, 
            () -> ekycService.verifyOtp(referenceNumber, invalidOtp));
        
        assertTrue(exception.getMessage().contains("Invalid OTP"));
    }

    @Test
    @DisplayName("Should throw exception for expired OTP")
    void testVerifyOtpWithExpiredOtp() {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        // Generate OTP
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        
        // Manually expire the OTP by updating the timestamp
        EkycRequest request = ekycRequestRepository.findByReferenceNumber(referenceNumber).get();
        request.setOtpGeneratedAt(LocalDateTime.now().minusMinutes(15)); // Assuming OTP expires in 10 minutes
        ekycRequestRepository.save(request);

        // When & Then
        EkycException exception = assertThrows(EkycException.class, 
            () -> ekycService.verifyOtp(referenceNumber, otp));
        
        assertTrue(exception.getMessage().contains("OTP has expired"));
    }

    @Test
    @DisplayName("Should complete eKYC verification successfully")
    void testCompleteVerification() {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        // Verify OTP
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        ekycService.verifyOtp(referenceNumber, otp);

        // When
        EkycResponseDto response = ekycService.completeVerification(referenceNumber);

        // Then
        assertNotNull(response);
        assertEquals(referenceNumber, response.getReferenceNumber());
        assertEquals(EkycStatus.COMPLETED, response.getStatus());

        // Verify updated in repository
        Optional<EkycRequest> updatedRequest = ekycRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(updatedRequest.isPresent());
        assertEquals(EkycStatus.COMPLETED, updatedRequest.get().getStatus());
        assertNotNull(updatedRequest.get().getCompletedAt());
    }

    @Test
    @DisplayName("Should throw exception when completing verification without OTP verification")
    void testCompleteVerificationWithoutOtpVerification() {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        // When & Then
        EkycException exception = assertThrows(EkycException.class, 
            () -> ekycService.completeVerification(referenceNumber));
        
        assertTrue(exception.getMessage().contains("OTP verification required"));
    }

    @Test
    @DisplayName("Should get eKYC request by reference number")
    void testGetEkycRequestByReferenceNumber() {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();

        // When
        EkycResponseDto response = ekycService.getEkycRequestByReferenceNumber(referenceNumber);

        // Then
        assertNotNull(response);
        assertEquals(referenceNumber, response.getReferenceNumber());
        assertEquals(EkycStatus.INITIATED, response.getStatus());
        
        // Verify PII is masked
        assertNotEquals(validRequestDto.getAadhaarNumber(), response.getAadhaarNumber());
        assertNotEquals(validRequestDto.getPanNumber(), response.getPanNumber());
        assertNotEquals(validRequestDto.getMobileNumber(), response.getMobileNumber());
        assertTrue(response.getAadhaarNumber().contains("X"));
        assertTrue(response.getPanNumber().contains("X"));
        assertTrue(response.getMobileNumber().contains("X"));
    }

    @Test
    @DisplayName("Should throw exception for non-existent reference number")
    void testGetEkycRequestByNonExistentReferenceNumber() {
        // Given
        String nonExistentReferenceNumber = "NON_EXISTENT_REF";

        // When & Then
        EkycException exception = assertThrows(EkycException.class, 
            () -> ekycService.getEkycRequestByReferenceNumber(nonExistentReferenceNumber));
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should get all eKYC requests by status")
    void testGetAllEkycRequestsByStatus() {
        // Given
        ekycService.createEkycRequest(validRequestDto);
        
        // Create another request and complete it
        EkycRequestDto anotherRequest = new EkycRequestDto();
        anotherRequest.setFullName("Jane Doe");
        anotherRequest.setEmail("jane.doe@example.com");
        anotherRequest.setMobileNumber("9876543211");
        anotherRequest.setAadhaarNumber("123456789013");
        anotherRequest.setPanNumber("ABCDE1234G");
        anotherRequest.setDateOfBirth(LocalDate.of(1992, 2, 2));
        anotherRequest.setAddress("456 Main St, Bangalore, Karnataka, 560001");
        
        EkycResponseDto anotherResponse = ekycService.createEkycRequest(anotherRequest);
        String referenceNumber = anotherResponse.getReferenceNumber();
        
        String otp = otpService.generateAndSendOtp(referenceNumber, anotherRequest.getMobileNumber());
        ekycService.verifyOtp(referenceNumber, otp);
        ekycService.completeVerification(referenceNumber);

        // When
        List<EkycResponseDto> initiatedRequests = ekycService.getAllEkycRequestsByStatus(EkycStatus.INITIATED);
        List<EkycResponseDto> completedRequests = ekycService.getAllEkycRequestsByStatus(EkycStatus.COMPLETED);

        // Then
        assertFalse(initiatedRequests.isEmpty());
        assertFalse(completedRequests.isEmpty());
        assertEquals(1, initiatedRequests.size());
        assertEquals(1, completedRequests.size());
        
        assertEquals(EkycStatus.INITIATED, initiatedRequests.get(0).getStatus());
        assertEquals(EkycStatus.COMPLETED, completedRequests.get(0).getStatus());
    }

    @ParameterizedTest
    @CsvSource({
        "John Doe, john.doe@example.com, 9876543210, 123456789012, ABCDE1234F, 1990-01-01, 123 Main St",
        "Jane Smith, jane.smith@example.com, 9876543211, 123456789013, ABCDE1234G, 1985-05-15, 456 Park Ave"
    })
    @DisplayName("Should create eKYC request with various valid inputs")
    void testCreateEkycRequestWithVariousValidInputs(String fullName, String email, String mobile, 
                                                    String aadhaar, String pan, String dob, String address) {
        // Given
        EkycRequestDto requestDto = new EkycRequestDto();
        requestDto.setFullName(fullName);
        requestDto.setEmail(email);
        requestDto.setMobileNumber(mobile);
        requestDto.setAadhaarNumber(aadhaar);
        requestDto.setPanNumber(pan);
        requestDto.setDateOfBirth(LocalDate.parse(dob));
        requestDto.setAddress(address);

        // When
        EkycResponseDto response = ekycService.createEkycRequest(requestDto);

        // Then
        assertNotNull(response);
        assertNotNull(response.getReferenceNumber());
        assertEquals(EkycStatus.INITIATED, response.getStatus());
    }

    @Test
    @DisplayName("Should handle concurrent eKYC requests")
    void testConcurrentEkycRequests() {
        // Create multiple threads to simulate concurrent requests
        Runnable task = () -> {
            EkycRequestDto requestDto = new EkycRequestDto();
            requestDto.setFullName("Thread User");
            requestDto.setEmail("thread" + Thread.currentThread().getId() + "@example.com");
            requestDto.setMobileNumber("9" + (1000000000 + Thread.currentThread().getId()));
            requestDto.setAadhaarNumber("1234" + (10000000 + Thread.currentThread().getId()));
            requestDto.setPanNumber("ABCDE" + Thread.currentThread().getId() + "F");
            requestDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
            requestDto.setAddress("Thread Address " + Thread.currentThread().getId());
            
            EkycResponseDto response = ekycService.createEkycRequest(requestDto);
            assertNotNull(response.getReferenceNumber());
        };

        // Create and start 5 threads
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(task);
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                fail("Thread interrupted: " + e.getMessage());
            }
        }

        // Verify all requests were saved
        List<EkycRequest> allRequests = ekycRequestRepository.findAll();
        assertTrue(allRequests.size() >= 5);
    }

    @Test
    @DisplayName("Should enforce data retention policy")
    void testDataRetentionPolicy() {
        // Given
        EkycResponseDto response = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = response.getReferenceNumber();
        
        // Complete the verification
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        ekycService.verifyOtp(referenceNumber, otp);
        ekycService.completeVerification(referenceNumber);
        
        // Manually set the completed date to be older than retention period (e.g., 7 years ago)
        EkycRequest request = ekycRequestRepository.findByReferenceNumber(referenceNumber).get();
        request.setCompletedAt(LocalDateTime.now().minusYears(7).minusDays(1));
        ekycRequestRepository.save(request);
        
        // When
        ekycService.applyDataRetentionPolicy();
        
        // Then
        Optional<EkycRequest> deletedRequest = ekycRequestRepository.findByReferenceNumber(referenceNumber);
        assertFalse(deletedRequest.isPresent(), "Request should be deleted after retention period");
    }

    @Test
    @DisplayName("Should not delete data within retention period")
    void testDataRetentionPolicyPreservesRecentData() {
        // Given
        EkycResponseDto response = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = response.getReferenceNumber();
        
        // Complete the verification
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        ekycService.verifyOtp(referenceNumber, otp);
        ekycService.completeVerification(referenceNumber);
        
        // When
        ekycService.applyDataRetentionPolicy();
        
        // Then
        Optional<EkycRequest> preservedRequest = ekycRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(preservedRequest.isPresent(), "Recent request should be preserved");
    }
}