package com.ekyc.service;

import com.ekyc.domain.OtpRequest;
import com.ekyc.domain.OtpStatus;
import com.ekyc.repository.OtpRequestRepository;
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

    private String phoneNumber;

    @BeforeEach
    public void setUp() {
        phoneNumber = "+1234567890";
    }

    @Test
    public void testGenerateOtp_ValidPhoneNumber_Success() {
        OtpRequest otpRequest = otpService.generateOtp(phoneNumber);
        
        assertNotNull(otpRequest);
        assertEquals(phoneNumber, otpRequest.getPhoneNumber());
        assertEquals(OtpStatus.GENERATED, otpRequest.getStatus());
        assertNotNull(otpRequest.getOtpCode());
    }

    @Test
    public void testValidateOtp_CorrectOtp_Success() {
        OtpRequest generatedOtp = otpService.generateOtp(phoneNumber);
        
        boolean validationResult = otpService.validateOtp(
            phoneNumber, 
            generatedOtp.getOtpCode()
        );
        
        assertTrue(validationResult);
    }

    @Test
    public void testValidateOtp_IncorrectOtp_Failure() {
        otpService.generateOtp(phoneNumber);
        
        boolean validationResult = otpService.validateOtp(
            phoneNumber, 
            "INCORRECT_OTP"
        );
        
        assertFalse(validationResult);
    }

    @Test
    public void testOtpExpiration() {
        OtpRequest expiredOtp = createExpiredOtpRequest();
        
        boolean validationResult = otpService.validateOtp(
            expiredOtp.getPhoneNumber(), 
            expiredOtp.getOtpCode()
        );
        
        assertFalse(validationResult);
    }

    private OtpRequest createExpiredOtpRequest() {
        OtpRequest request = new OtpRequest();
        request.setPhoneNumber(phoneNumber);
        request.setOtpCode("123456");
        request.setCreatedAt(LocalDateTime.now().minusMinutes(20));
        request.setStatus(OtpStatus.EXPIRED);
        return otpRequestRepository.save(request);
    }
}