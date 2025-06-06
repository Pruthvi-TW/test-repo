package com.ekyc.service;

import com.ekyc.exception.UidaiServiceException;
import com.ekyc.model.OtpVerificationRecord;
import com.ekyc.model.UidaiOtpVerificationResponse;
import com.ekyc.repository.OtpVerificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service responsible for handling OTP verification operations.
 * This includes validating OTPs with UIDAI and maintaining verification records.
 */
@Service
public class OtpService {
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    
    private final UidaiIntegrationService uidaiIntegrationService;
    private final AuditService auditService;
    private final OtpVerificationRepository otpVerificationRepository;
    
    @Autowired
    public OtpService(UidaiIntegrationService uidaiIntegrationService,
                     AuditService auditService,
                     OtpVerificationRepository otpVerificationRepository) {
        this.uidaiIntegrationService = uidaiIntegrationService;
        this.auditService = auditService;
        this.otpVerificationRepository = otpVerificationRepository;
    }
    
    /**
     * Verifies an OTP with the UIDAI service.
     * 
     * @param otp The OTP to verify
     * @param idNumber The Aadhaar/VID number
     * @param idType The type of ID (AADHAAR or VID)
     * @param referenceNumber The reference number from the eKYC initiation
     * @return true if OTP verification is successful, false otherwise
     * @throws UidaiServiceException if there's an error communicating with UIDAI
     */
    @Transactional
    public boolean verifyOtp(String otp, String idNumber, String idType, String referenceNumber) {
        String maskedId = auditService.maskAadhaarOrVid(idNumber);
        String verificationId = UUID.randomUUID().toString();
        
        logger.info("Verifying OTP for reference: {}, verification ID: {}", referenceNumber, verificationId);
        
        try {
            // Create OTP verification record
            OtpVerificationRecord verificationRecord = new OtpVerificationRecord();
            verificationRecord.setVerificationId(verificationId);
            verificationRecord.setReferenceNumber(referenceNumber);
            verificationRecord.setOtpHash(hashOtp(otp)); // Store hashed OTP for audit
            verificationRecord.setCreatedAt(LocalDateTime.now());
            verificationRecord.setAttemptCount(1);
            
            // Save initial record
            otpVerificationRepository.save(verificationRecord);
            
            // Call UIDAI to verify OTP
            UidaiOtpVerificationResponse response = uidaiIntegrationService.verifyOtp(
                    otp, idNumber, idType, referenceNumber);
            
            // Update verification record with result
            verificationRecord.setVerified(response.isSuccess());
            verificationRecord.setCompletedAt(LocalDateTime.now());
            verificationRecord.setResponseHash(hashResponse(response.getResponseData()));
            
            if (!response.isSuccess()) {
                verificationRecord.setFailureReason(response.getErrorMessage());
                auditService.logFailure("OTP_VERIFICATION", verificationId, maskedId,
                        "OTP verification failed: " + response.getErrorMessage());
            } else {
                auditService.logSuccess("OTP_VERIFICATION", verificationId, maskedId,
                        "OTP verification successful");
            }
            
            // Save updated record
            otpVerificationRepository.save(verificationRecord);
            
            return response.isSuccess();
        } catch (Exception e) {
            auditService.logFailure("OTP_VERIFICATION", verificationId, maskedId,
                    "Error during OTP verification: " + e.getMessage());
            throw new UidaiServiceException("Failed to verify OTP with UIDAI", e);
        }
    }
    
    /**
     * Checks if a given OTP verification has exceeded the maximum allowed attempts.
     * 
     * @param referenceNumber The reference number from the eKYC initiation
     * @return true if max attempts exceeded, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean hasExceededMaxAttempts(String referenceNumber) {
        logger.info("Checking OTP attempt count for reference: {}", referenceNumber);
        
        int maxAttempts = 3; // This could be configurable
        int attemptCount = otpVerificationRepository.countByReferenceNumber(referenceNumber);
        
        if (attemptCount >= maxAttempts) {
            auditService.logWarning("OTP_VERIFICATION", "N/A", "N/A",
                    "Max OTP attempts exceeded for reference: " + referenceNumber);
            return true;
        }
        
        return false;
    }
    
    /**
     * Securely hashes an OTP for storage.
     * 
     * @param otp The OTP to hash
     * @return The hashed OTP
     */
    private String hashOtp(String otp) {
        // In a real implementation, use a secure hashing algorithm with salt
        // This is a simplified example
        return "hashed_" + otp;
    }
    
    /**
     * Securely hashes response data for audit storage.
     * 
     * @param responseData The response data to hash
     * @return The hashed response data
     */
    private String hashResponse(String responseData) {
        // In a real implementation, use a secure hashing algorithm
        // This is a simplified example
        return "hashed_response_" + (responseData != null ? responseData.hashCode() : "null");
    }
}