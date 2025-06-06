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
            boolean uidaiResponse = uidaiService.initiateVerification(request);
            
            if (uidaiResponse) {
                // Generate and send OTP
                otpService.generateAndSendOtp(request.getIdNumber(), referenceNumber);
                response.setStatus(VerificationStatus.OTP_GENERATED);
            } else {
                response.setStatus(VerificationStatus.FAILED);
                response.setErrorCode("UIDAI_VERIFICATION_FAILED");
            }
            
            auditService.logEkycInitiation(request, response);
            return response;
            
        } catch (Exception e) {
            logger.error("Error during eKYC initiation for ID: {}", maskedId, e);
            throw new EkycException("Failed to initiate eKYC", e);
        }
    }

    @Transactional
    public EkycResponse verifyOtp(String referenceNumber, String otp) {
        logger.info("Processing OTP verification for reference: {}", referenceNumber);
        
        try {
            // Phase 2: OTP Verification
            validationService.validateOtp(otp);
            boolean otpValid = otpService.verifyOtp(referenceNumber, otp);
            
            EkycResponse response = new EkycResponse();
            response.setReferenceNumber(referenceNumber);
            
            if (otpValid) {
                response.setStatus(VerificationStatus.VERIFIED);
                // Process UIDAI response and hash sensitive data
                uidaiService.processVerificationResponse(referenceNumber);
            } else {
                response.setStatus(VerificationStatus.FAILED);
                response.setErrorCode("INVALID_OTP");
            }
            
            auditService.logOtpVerification(referenceNumber, response);
            return response;
            
        } catch (Exception e) {
            logger.error("Error during OTP verification for reference: {}", referenceNumber, e);
            throw new EkycException("Failed to verify OTP", e);
        }
    }

    private String generateReferenceNumber() {
        return "EKYC" + System.currentTimeMillis();
    }

    private String maskSensitiveData(String data) {
        if (data == null || data.length() < 4) {
            return "****";
        }
        return "XXXX" + data.substring(data.length() - 4);
    }
}
```