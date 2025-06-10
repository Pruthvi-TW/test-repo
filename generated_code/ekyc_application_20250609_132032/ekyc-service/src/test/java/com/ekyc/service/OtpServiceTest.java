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
        assertTrue(otpRequest.getExpiryTime().isAfter(LocalDateTime.now()));
    }

    @Test
    public void testValidateOtp_CorrectOtp_Success() {
        OtpRequest generatedOtp = otpService.generateOtp(phoneNumber);
        
        boolean isValid = otpService.validateOtp(
            phoneNumber, 
            generatedOtp.getOtpCode()
        );
        
        assertTrue(isValid);
    }

    @Test
    public void testValidateOtp_IncorrectOtp_Failure() {
        otpService.generateOtp(phoneNumber);
        
        boolean isValid = otpService.validateOtp(
            phoneNumber, 
            "INCORRECT_OTP"
        );
        
        assertFalse(isValid);
    }

    @Test
    public void testValidateOtp_ExpiredOtp_Failure() {
        // Simulate OTP expiry by creating an expired OTP record
        OtpRequest expiredOtp = new OtpRequest();
        expiredOtp.setPhoneNumber(phoneNumber);
        expiredOtp.setOtpCode("123456");
        expiredOtp.setExpiryTime(LocalDateTime.now().minusMinutes(10));
        expiredOtp.setStatus(OtpStatus.GENERATED);
        
        otpRequestRepository.save(expiredOtp);
        
        boolean isValid = otpService.validateOtp(
            phoneNumber, 
            expiredOtp.getOtpCode()
        );
        
        assertFalse(isValid);
    }
}