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

import java.time.LocalDateTime;

@Service
public class EkycService {
    private static final Logger logger = LoggerFactory.getLogger(EkycService.class);
    
    private final ValidationService validationService;
    private final UidaiIntegrationService uidaiService;
    private final AuditService auditService;
    private final SessionService sessionService;
    private final OtpService otpService;

    public EkycService(ValidationService validationService,
                      UidaiIntegrationService uidaiService,
                      AuditService auditService,
                      SessionService sessionService,
                      OtpService otpService) {
        this.validationService = validationService;
        this.uidaiService = uidaiService;
        this.auditService = auditService;
        this.sessionService = sessionService;
        this.otpService = otpService;
    }

    @Transactional
    public EkycResponse initiateEkyc(EkycRequest request) {
        String maskedId = maskSensitiveData(request.getIdNumber());
        logger.info("Initiating eKYC request for ID: {}", maskedId);
        
        try {
            // Phase 1: Validation
            validationService.validateEkycRequest(request);
            sessionService.validateAndCreateSession(request.getSessionId());
            
            // Create eKYC record
            String referenceNumber = generateReferenceNumber();
            EkycResponse response = new EkycResponse();
            response.setReferenceNumber(referenceNumber);
            response.setStatus(VerificationStatus.IN_PROGRESS);
            
            // Call UIDAI
            boolean uidaiInitiated = uidaiService.initiateVerification(request);
            
            if (!uidaiInitiated) {
                throw new EkycException("Failed to initiate UIDAI verification");
            }
            
            // Audit successful initiation
            auditService.logEkycInitiation(referenceNumber, request);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error during eKYC initiation for ID: {}", maskedId, e);
            auditService.logFailure(request, e.getMessage());
            throw new EkycException("eKYC initiation failed", e);
        }
    }

    @Transactional
    public EkycResponse verifyOtp(String referenceNumber, String otp) {
        logger.info("Processing OTP verification for reference: {}", referenceNumber);
        
        try {
            // Phase 2: OTP Verification
            validationService.validateOtp(otp);
            
            // Verify session is still valid
            sessionService.validateSession(referenceNumber);
            
            // Process OTP verification
            boolean otpVerified = otpService.verifyOtp(referenceNumber, otp);
            
            if (!otpVerified) {
                throw new ValidationException("Invalid OTP");
            }
            
            // Call UIDAI for final verification
            EkycResponse response = uidaiService.completeVerification(referenceNumber);
            
            // Audit successful verification
            auditService.logSuccessfulVerification(referenceNumber);
            
            return response;
            
        } catch (Exception e) {
            logger.error("OTP verification failed for reference: {}", referenceNumber, e);
            auditService.logFailure(referenceNumber, e.getMessage());
            throw new EkycException("OTP verification failed", e);
        }
    }

    private String maskSensitiveData(String data) {
        if (data == null || data.length() < 4) {
            return "****";
        }
        return "XXXX" + data.substring(data.length() - 4);
    }

    private String generateReferenceNumber() {
        return "EKYC" + System.currentTimeMillis();
    }
}
```