```java
package com.ekyc.service;

import com.ekyc.exception.EkycException;
import com.ekyc.exception.ValidationException;
import com.ekyc.model.EkycRequest;
import com.ekyc.model.EkycStatus;
import com.ekyc.repository.EkycRequestRepository;
import com.ekyc.util.MaskingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class EkycService {
    private static final Logger LOGGER = Logger.getLogger(EkycService.class.getName());

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UidaiIntegrationService uidaiService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private EkycRequestRepository ekycRequestRepository;

    @Transactional
    public EkycRequest initiateEkycRequest(String identityNumber, String idType, 
                                           boolean identityConsent, 
                                           boolean contactConsent) {
        // Validate input
        validationService.validateIdentityNumber(identityNumber, idType);
        validationService.validateConsent(identityConsent, contactConsent);

        // Create eKYC request
        EkycRequest request = new EkycRequest();
        request.setId(UUID.randomUUID().toString());
        request.setIdentityNumber(MaskingUtil.maskIdentityNumber(identityNumber));
        request.setIdType(idType);
        request.setIdentityConsent(identityConsent);
        request.setContactConsent(contactConsent);
        request.setStatus(EkycStatus.INITIATED);
        request.setCreatedAt(LocalDateTime.now());

        try {
            // Call UIDAI API
            String referenceNumber = uidaiService.initiateEkycProcess(request);
            request.setReferenceNumber(referenceNumber);
            request.setStatus(EkycStatus.IN_PROGRESS);

            // Audit the request
            auditService.logEkycRequestInitiation(request);

            // Persist request
            return ekycRequestRepository.save(request);
        } catch (Exception e) {
            request.setStatus(EkycStatus.FAILED);
            request.setErrorReason(e.getMessage());
            
            // Log error
            LOGGER.severe("eKYC Request Initiation Failed: " + 
                MaskingUtil.maskLog(e.getMessage()));
            
            // Audit failure
            auditService.logEkycRequestFailure(request);

            throw new EkycException("Failed to initiate eKYC", e);
        }
    }

    @Transactional
    public EkycRequest verifyOtp(String referenceNumber, String otp) {
        // Validate OTP
        validationService.validateOtp(otp);

        // Retrieve existing request
        EkycRequest request = ekycRequestRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new ValidationException("Invalid Reference Number"));

        try {
            // Verify OTP with UIDAI
            boolean otpVerified = uidaiService.verifyOtp(referenceNumber, otp);

            if (otpVerified) {
                request.setStatus(EkycStatus.VERIFIED);
                auditService.logOtpVerification(request, true);
            } else {
                request.setStatus(EkycStatus.FAILED);
                request.setErrorReason("INVALID_OTP");
                auditService.logOtpVerification(request, false);
            }

            return ekycRequestRepository.save(request);
        } catch (Exception e) {
            LOGGER.severe("OTP Verification Failed: " + 
                MaskingUtil.maskLog(e.getMessage()));
            
            request.setStatus(EkycStatus.FAILED);
            request.setErrorReason(e.getMessage());
            
            auditService.logEkycRequestFailure(request);
            
            throw new EkycException("OTP Verification Failed", e);
        }
    }
}
```