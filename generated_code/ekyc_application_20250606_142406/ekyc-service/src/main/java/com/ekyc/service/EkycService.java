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
        String maskedId = maskAadhaarId(request.getIdNumber());
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
            
            // Call UIDAI API
            boolean uidaiResponse = uidaiService.initiateEkyc(request);
            
            if (uidaiResponse) {
                response.setStatus(VerificationStatus.AWAITING_OTP);
                auditService.logSuccess("eKYC_INITIATION", referenceNumber, maskedId);
            } else {
                response.setStatus(VerificationStatus.FAILED);
                auditService.logFailure("eKYC_INITIATION", referenceNumber, maskedId, "UIDAI API failure");
            }
            
            return response;
            
        } catch (Exception e) {
            auditService.logFailure("eKYC_INITIATION", "N/A", maskedId, e.getMessage());
            throw new EkycException("Failed to initiate eKYC", e);
        }
    }

    @Transactional
    public EkycResponse verifyOtp(String referenceNumber, String otp) {
        logger.info("Processing OTP verification for reference: {}", referenceNumber);
        
        try {
            // Phase 2: OTP Verification
            validationService.validateOtp(otp);
            sessionService.validateSession(referenceNumber);
            
            boolean otpVerified = otpService.verifyOtp(referenceNumber, otp);
            
            EkycResponse response = new EkycResponse();
            response.setReferenceNumber(referenceNumber);
            
            if (otpVerified) {
                response.setStatus(VerificationStatus.VERIFIED);
                auditService.logSuccess("OTP_VERIFICATION", referenceNumber, null);
            } else {
                response.setStatus(VerificationStatus.FAILED);
                auditService.logFailure("OTP_VERIFICATION", referenceNumber, null, "Invalid OTP");
            }
            
            return response;
            
        } catch (Exception e) {
            auditService.logFailure("OTP_VERIFICATION", referenceNumber, null, e.getMessage());
            throw new EkycException("OTP verification failed", e);
        }
    }

    private String maskAadhaarId(String aadhaarId) {
        if (aadhaarId == null || aadhaarId.length() != 12) {
            return "INVALID_ID";
        }
        return "XXXXXXXX" + aadhaarId.substring(8);
    }

    private String generateReferenceNumber() {
        return "EKYC" + System.currentTimeMillis();
    }
}
```