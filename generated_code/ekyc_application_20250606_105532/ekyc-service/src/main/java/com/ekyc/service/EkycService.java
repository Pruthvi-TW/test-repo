```java
package com.ekyc.service;

import com.ekyc.exception.EkycException;
import com.ekyc.exception.ValidationException;
import com.ekyc.model.EkycRequest;
import com.ekyc.model.EkycResponse;
import com.ekyc.model.VerificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Core service handling eKYC verification workflow.
 */
@Service
public class EkycService {
    private static final Logger logger = LoggerFactory.getLogger(EkycService.class);
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("^[0-9]{12}$");
    
    private final UidaiIntegrationService uidaiService;
    private final OtpService otpService;
    private final AuditService auditService;
    
    public EkycService(UidaiIntegrationService uidaiService, 
                      OtpService otpService,
                      AuditService auditService) {
        this.uidaiService = uidaiService;
        this.otpService = otpService;
        this.auditService = auditService;
    }

    /**
     * Initiates eKYC verification process.
     * @param request The eKYC request containing Aadhaar/VID and consent details
     * @return EkycResponse with reference number and status
     */
    @Transactional
    public EkycResponse initiateEkyc(EkycRequest request) {
        logger.info("Initiating eKYC for session: {}", request.getSessionId());
        
        validateRequest(request);
        
        try {
            auditService.logRequestInitiation(request);
            
            EkycResponse response = uidaiService.initiateVerification(request);
            
            if (response.getStatus() == VerificationStatus.IN_PROGRESS) {
                otpService.handleOtpDelivery(response.getReferenceNumber(), 
                    request.isEmailConsentGiven(), 
                    request.isMobileConsentGiven());
            }
            
            auditService.logResponse(response);
            return response;
            
        } catch (Exception e) {
            logger.error("Error during eKYC initiation: {}", e.getMessage());
            auditService.logFailure(request.getSessionId(), e);
            throw new EkycException("Failed to initiate eKYC", e);
        }
    }

    /**
     * Verifies OTP for ongoing eKYC request.
     * @param referenceNumber eKYC reference number
     * @param otp OTP received by user
     * @return Updated verification status
     */
    @Transactional
    public EkycResponse verifyOtp(String referenceNumber, String otp) {
        logger.info("Processing OTP verification for ref: {}", referenceNumber);
        
        try {
            otpService.validateOtp(otp);
            
            EkycResponse response = uidaiService.verifyOtp(referenceNumber, otp);
            auditService.logOtpVerification(referenceNumber, response.getStatus());
            
            return response;
            
        } catch (Exception e) {
            logger.error("OTP verification failed for ref {}: {}", 
                referenceNumber, e.getMessage());
            auditService.logFailure(referenceNumber, e);
            throw new EkycException("OTP verification failed", e);
        }
    }

    private void validateRequest(EkycRequest request) {
        if (request == null) {
            throw new ValidationException("Request cannot be null");
        }
        
        if (!AADHAAR_PATTERN.matcher(request.getIdNumber()).matches()) {
            throw new ValidationException("Invalid ID format");
        }
        
        if (!request.isIdentityVerificationConsent()) {
            throw new ValidationException("Identity verification consent is mandatory");
        }
        
        if (request.getSessionId() == null || request.getSessionId().trim().isEmpty()) {
            throw new ValidationException("Session ID is mandatory");
        }
    }

    /**
     * Retrieves current status of eKYC request.
     * @param referenceNumber eKYC reference number
     * @return Current verification status
     */
    public EkycResponse getStatus(String referenceNumber) {
        logger.info("Fetching status for ref: {}", referenceNumber);
        
        try {
            EkycResponse response = uidaiService.checkStatus(referenceNumber);
            auditService.logStatusCheck(referenceNumber, response.getStatus());
            return response;
            
        } catch (Exception e) {
            logger.error("Status check failed for ref {}: {}", 
                referenceNumber, e.getMessage());
            throw new EkycException("Failed to retrieve status", e);
        }
    }
}
```