package com.ekyc.service;

import com.ekyc.exception.EkycException;
import com.ekyc.model.OtpVerification;
import com.ekyc.repository.OtpVerificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service responsible for handling OTP-related operations.
 * This includes storing and validating OTP verification attempts.
 */
@Service
public class OtpService {
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    
    private final OtpVerificationRepository otpVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    
    @Autowired
    public OtpService(OtpVerificationRepository otpVerificationRepository,
                     PasswordEncoder passwordEncoder,
                     AuditService auditService) {
        this.otpVerificationRepository = otpVerificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }
    
    /**
     * Stores an OTP verification attempt.
     * The OTP is hashed before storage for security.
     * 
     * @param referenceNumber The reference number of the eKYC request
     * @param otp The OTP to be stored (will be hashed)
     * @param isVerified Whether the OTP verification was successful
     * @param failureReason The reason for failure, if applicable
     * @return The created OtpVerification entity
     * @throws EkycException if there's an error during processing
     */
    @Transactional
    public OtpVerification storeOtpVerification(String referenceNumber, String otp, 
                                               boolean isVerified, String failureReason) {
        logger.info("Storing OTP verification for reference: {}, verified: {}", 
                referenceNumber, isVerified);
        
        try {
            // Hash the OTP before storing
            String hashedOtp = passwordEncoder.encode(otp);
            
            OtpVerification otpVerification = new OtpVerification();
            otpVerification.setReferenceNumber(referenceNumber);
            otpVerification.setHashedOtp(hashedOtp);
            otpVerification.setVerified(isVerified);
            otpVerification.setFailureReason(failureReason);
            otpVerification.setVerificationTime(LocalDateTime.now());
            
            otpVerificationRepository.save(otpVerification);
            
            auditService.logInfo("OTP verification stored", null, referenceNumber);
            
            return otpVerification;
        } catch (Exception e) {
            auditService.logFailure("Failed to store OTP verification", 
                    null, referenceNumber, e.getMessage());
            throw new EkycException("Failed to store OTP verification: " + e.getMessage(), e);
        }
    }
    
    /**
     * Checks if an OTP verification attempt exists for a given reference number.
     * 
     * @param referenceNumber The reference number of the eKYC request
     * @return true if an OTP verification attempt exists, false otherwise
     */
    public boolean hasOtpVerificationAttempt(String referenceNumber) {
        return otpVerificationRepository.existsByReferenceNumber(referenceNumber);
    }
    
    /**
     * Retrieves the number of failed OTP verification attempts for a given reference number.
     * 
     * @param referenceNumber The reference number of the eKYC request
     * @return The number of failed OTP verification attempts
     */
    public int getFailedAttemptCount(String referenceNumber) {
        return otpVerificationRepository.countByReferenceNumberAndVerifiedFalse(referenceNumber);
    }
    
    /**
     * Checks if the maximum number of OTP verification attempts has been reached.
     * 
     * @param referenceNumber The reference number of the eKYC request
     * @param maxAttempts The maximum number of attempts allowed
     * @return true if the maximum number of attempts has been reached, false otherwise
     */
    public boolean isMaxAttemptsReached(String referenceNumber, int maxAttempts) {
        int failedAttempts = getFailedAttemptCount(referenceNumber);
        return failedAttempts >= maxAttempts;
    }
}