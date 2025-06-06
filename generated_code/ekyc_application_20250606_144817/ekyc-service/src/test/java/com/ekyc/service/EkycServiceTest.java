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
    @DisplayName("Should successfully create eKYC request with valid data")
    void testCreateEkycRequest_Success() {
        // When
        EkycResponseDto response = ekycService.createEkycRequest(validRequestDto);

        // Then
        assertNotNull(response);
        assertNotNull(response.getReferenceNumber());
        assertEquals(EkycStatus.INITIATED, response.getStatus());

        // Verify data is saved in repository
        Optional<EkycRequest> savedRequest = ekycRequestRepository.findByReferenceNumber(response.getReferenceNumber());
        assertTrue(savedRequest.isPresent());
        assertEquals(validRequestDto.getFullName(), savedRequest.get().getFullName());
        assertEquals(validRequestDto.getEmail(), savedRequest.get().getEmail());
    }

    @Test
    @DisplayName("Should throw ValidationException when creating eKYC request with invalid Aadhaar")
    void testCreateEkycRequest_InvalidAadhaar() {
        // Given
        validRequestDto.setAadhaarNumber("12345"); // Invalid Aadhaar

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> ekycService.createEkycRequest(validRequestDto));
        
        assertTrue(exception.getMessage().contains("Aadhaar number"));
    }

    @Test
    @DisplayName("Should throw ValidationException when creating eKYC request with invalid PAN")
    void testCreateEkycRequest_InvalidPan() {
        // Given
        validRequestDto.setPanNumber("INVALID"); // Invalid PAN

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> ekycService.createEkycRequest(validRequestDto));
        
        assertTrue(exception.getMessage().contains("PAN number"));
    }

    @Test
    @DisplayName("Should throw ValidationException when creating eKYC request with future date of birth")
    void testCreateEkycRequest_FutureDateOfBirth() {
        // Given
        validRequestDto.setDateOfBirth(LocalDate.now().plusDays(1)); // Future date

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> ekycService.createEkycRequest(validRequestDto));
        
        assertTrue(exception.getMessage().contains("Date of birth"));
    }

    @Test
    @DisplayName("Should verify OTP successfully")
    void testVerifyOtp_Success() {
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

        // Verify status updated in repository
        Optional<EkycRequest> updatedRequest = ekycRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(updatedRequest.isPresent());
        assertEquals(EkycStatus.OTP_VERIFIED, updatedRequest.get().getStatus());
    }

    @Test
    @DisplayName("Should throw exception when verifying with invalid OTP")
    void testVerifyOtp_InvalidOtp() {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        String invalidOtp = "999999"; // Invalid OTP

        // When & Then
        EkycException exception = assertThrows(EkycException.class, 
            () -> ekycService.verifyOtp(referenceNumber, invalidOtp));
        
        assertTrue(exception.getMessage().contains("Invalid OTP"));
    }

    @Test
    @DisplayName("Should throw exception when verifying OTP for non-existent reference number")
    void testVerifyOtp_NonExistentReference() {
        // Given
        String nonExistentReference = "NON_EXISTENT_REF";
        String otp = "123456";

        // When & Then
        EkycException exception = assertThrows(EkycException.class, 
            () -> ekycService.verifyOtp(nonExistentReference, otp));
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should complete eKYC verification successfully")
    void testCompleteVerification_Success() {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        // Generate and verify OTP
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        ekycService.verifyOtp(referenceNumber, otp);

        // When
        EkycResponseDto response = ekycService.completeVerification(referenceNumber);

        // Then
        assertNotNull(response);
        assertEquals(referenceNumber, response.getReferenceNumber());
        assertEquals(EkycStatus.COMPLETED, response.getStatus());

        // Verify status updated in repository
        Optional<EkycRequest> updatedRequest = ekycRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(updatedRequest.isPresent());
        assertEquals(EkycStatus.COMPLETED, updatedRequest.get().getStatus());
        assertNotNull(updatedRequest.get().getCompletedAt());
    }

    @Test
    @DisplayName("Should throw exception when completing verification without OTP verification")
    void testCompleteVerification_WithoutOtpVerification() {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();

        // When & Then
        EkycException exception = assertThrows(EkycException.class, 
            () -> ekycService.completeVerification(referenceNumber));
        
        assertTrue(exception.getMessage().contains("OTP verification"));
    }

    @Test
    @DisplayName("Should get eKYC request by reference number")
    void testGetEkycRequest_Success() {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();

        // When
        EkycResponseDto response = ekycService.getEkycRequest(referenceNumber);

        // Then
        assertNotNull(response);
        assertEquals(referenceNumber, response.getReferenceNumber());
        assertEquals(EkycStatus.INITIATED, response.getStatus());
        assertEquals(MaskingUtil.maskAadhaarNumber(validRequestDto.getAadhaarNumber()), response.getMaskedAadhaarNumber());
        assertEquals(MaskingUtil.maskPanNumber(validRequestDto.getPanNumber()), response.getMaskedPanNumber());
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent eKYC request")
    void testGetEkycRequest_NonExistent() {
        // Given
        String nonExistentReference = "NON_EXISTENT_REF";

        // When & Then
        EkycException exception = assertThrows(EkycException.class, 
            () -> ekycService.getEkycRequest(nonExistentReference));
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Should get all eKYC requests")
    void testGetAllEkycRequests() {
        // Given
        ekycService.createEkycRequest(validRequestDto);
        
        // Create another request
        EkycRequestDto anotherRequest = new EkycRequestDto();
        anotherRequest.setFullName("Jane Doe");
        anotherRequest.setEmail("jane.doe@example.com");
        anotherRequest.setMobileNumber("9876543211");
        anotherRequest.setAadhaarNumber("123456789013");
        anotherRequest.setPanNumber("ABCDE1234G");
        anotherRequest.setDateOfBirth(LocalDate.of(1992, 2, 2));
        anotherRequest.setAddress("456 Main St, Bangalore, Karnataka, 560001");
        ekycService.createEkycRequest(anotherRequest);

        // When
        List<EkycResponseDto> responses = ekycService.getAllEkycRequests();

        // Then
        assertNotNull(responses);
        assertTrue(responses.size() >= 2);
    }

    @ParameterizedTest
    @CsvSource({
        "John Doe, john.doe@example.com, 9876543210, 123456789012, ABCDE1234F, 1990-01-01, 123 Main St",
        "Jane Smith, jane.smith@example.com, 8765432109, 987654321098, PQRST5678G, 1985-05-15, 456 Park Ave"
    })
    @DisplayName("Should create eKYC requests with different valid data")
    void testCreateEkycRequest_MultipleValidData(String fullName, String email, String mobile, 
                                                String aadhaar, String pan, LocalDate dob, String address) {
        // Given
        EkycRequestDto requestDto = new EkycRequestDto();
        requestDto.setFullName(fullName);
        requestDto.setEmail(email);
        requestDto.setMobileNumber(mobile);
        requestDto.setAadhaarNumber(aadhaar);
        requestDto.setPanNumber(pan);
        requestDto.setDateOfBirth(dob);
        requestDto.setAddress(address);

        // When
        EkycResponseDto response = ekycService.createEkycRequest(requestDto);

        // Then
        assertNotNull(response);
        assertNotNull(response.getReferenceNumber());
        assertEquals(EkycStatus.INITIATED, response.getStatus());
    }

    @Test
    @DisplayName("Should enforce data retention policy")
    void testDataRetentionPolicy() {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        // Complete the verification
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        ekycService.verifyOtp(referenceNumber, otp);
        ekycService.completeVerification(referenceNumber);
        
        // Get the completed request
        Optional<EkycRequest> completedRequest = ekycRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(completedRequest.isPresent());
        
        // Simulate retention period by manually setting completedAt to past date
        EkycRequest request = completedRequest.get();
        request.setCompletedAt(LocalDateTime.now().minusDays(91)); // Beyond 90-day retention
        ekycRequestRepository.save(request);
        
        // When
        ekycService.applyRetentionPolicy();
        
        // Then
        Optional<EkycRequest> retainedRequest = ekycRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(retainedRequest.isPresent());
        
        // Verify sensitive data is cleared
        assertNull(retainedRequest.get().getAadhaarNumber());
        assertNull(retainedRequest.get().getPanNumber());
        // Reference data should be retained
        assertNotNull(retainedRequest.get().getReferenceNumber());
        assertEquals(EkycStatus.COMPLETED, retainedRequest.get().getStatus());
    }
}