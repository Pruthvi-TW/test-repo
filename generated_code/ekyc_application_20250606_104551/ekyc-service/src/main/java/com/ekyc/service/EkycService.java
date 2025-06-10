```java
package com.ekyc.service;

import com.ekyc.exception.EkycException;
import com.ekyc.exception.ValidationException;
import com.ekyc.model.EkycRequest;
import com.ekyc.model.EkycResponse;
import com.ekyc.model.EkycStatus;
import com.ekyc.repository.EkycRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class EkycService {

    private static final Pattern AADHAAR_PATTERN = Pattern.compile("^[0-9]{12}$");
    private static final Pattern VID_PATTERN = Pattern.compile("^[0-9]{12}$");
    
    private final EkycRepository ekycRepository;
    private final UidaiIntegrationService uidaiService;
    private final AuditService auditService;
    private final OtpService otpService;

    /**
     * Initiates an eKYC verification request
     * @param request The eKYC request details
     * @return EkycResponse containing reference number and status
     * @throws ValidationException if request validation fails
     * @throws EkycException if processing fails
     */
    @Transactional
    public EkycResponse initiateEkyc(EkycRequest request) {
        log.info("Initiating eKYC request for session: {}", request.getSessionId());
        
        validateRequest(request);
        
        try {
            // Create and persist request
            var ekycRecord = ekycRepository.save(mapToEntity(request));
            
            // Call UIDAI
            var uidaiResponse = uidaiService.initiateEkyc(request);
            
            // Update status based on response
            if (uidaiResponse.isSuccess()) {
                ekycRecord.setStatus(EkycStatus.IN_PROGRESS);
                ekycRecord.setReferenceNumber(uidaiResponse.getReferenceNumber());
                
                // Trigger OTP if consented
                if (request.isMobileEmailConsent()) {
                    otpService.triggerOtp(ekycRecord.getReferenceNumber());
                }
            } else {
                ekycRecord.setStatus(EkycStatus.FAILED);
                ekycRecord.setFailureReason(uidaiResponse.getErrorCode());
            }
            
            ekycRepository.save(ekycRecord);
            
            // Audit trail
            auditService.logEkycRequest(ekycRecord);
            
            return mapToResponse(ekycRecord);
            
        } catch (Exception e) {
            log.error("Error processing eKYC request: {}", e.getMessage());
            throw new EkycException("Failed to process eKYC request", e);
        }
    }

    private void validateRequest(EkycRequest request) {
        if (request.getIdType().equals("AADHAAR")) {
            if (!AADHAAR_PATTERN.matcher(request.getId()).matches()) {
                throw new ValidationException("Invalid Aadhaar number format");
            }
        } else if (request.getIdType().equals("VID")) {
            if (!VID_PATTERN.matcher(request.getId()).matches()) {
                throw new ValidationException("Invalid VID format");
            }
        } else {
            throw new ValidationException("Invalid ID type");
        }

        if (!request.isIdentityVerificationConsent()) {
            throw new ValidationException("Identity verification consent is mandatory");
        }
    }

    // Additional methods for status checks, response mapping etc.
}
```