```java
package com.ekyc.service;

import com.ekyc.model.EkycRequest;
import com.ekyc.model.EkycResponse;
import com.ekyc.model.VerificationStatus;
import com.ekyc.exception.EkycValidationException;
import com.ekyc.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class EkycService {
    private static final Pattern ID_PATTERN = Pattern.compile("^\\d{12}$");
    
    private final UidaiIntegrationService uidaiService;
    private final OtpService otpService;
    
    public EkycResponse initiateEkyc(EkycRequest request) {
        log.info("Initiating eKYC request for session: {}", request.getSessionId());
        validateRequest(request);
        
        String referenceId = UUID.randomUUID().toString();
        try {
            var uidaiResponse = uidaiService.initiateVerification(request);
            if (uidaiResponse.isSuccess()) {
                otpService.handleOtpDelivery(request, uidaiResponse);
                return EkycResponse.builder()
                    .status(VerificationStatus.IN_PROGRESS)
                    .referenceId(referenceId)
                    .build();
            }
            return handleFailedResponse(referenceId);
        } catch (Exception e) {
            log.error("Error during eKYC initiation: {}", e.getMessage());
            throw new ExternalServiceException("Failed to process eKYC request");
        }
    }

    public EkycResponse verifyOtp(String referenceId, String otp) {
        log.info("Processing OTP verification for reference: {}", referenceId);
        validateOtp(otp);
        
        try {
            boolean isVerified = uidaiService.verifyOtp(referenceId, otp);
            return EkycResponse.builder()
                .status(isVerified ? VerificationStatus.VERIFIED : VerificationStatus.FAILED)
                .referenceId(referenceId)
                .build();
        } catch (Exception e) {
            log.error("OTP verification failed: {}", e.getMessage());
            throw new ExternalServiceException("OTP verification failed");
        }
    }

    private void validateRequest(EkycRequest request) {
        if (!ID_PATTERN.matcher(request.getIdNumber()).matches()) {
            throw new EkycValidationException("Invalid ID format");
        }
        if (request.getIdType() == null) {
            throw new EkycValidationException("ID type must be specified");
        }
        if (!request.isIdentityVerificationConsent()) {
            throw new EkycValidationException("Identity verification consent is mandatory");
        }
    }

    private void validateOtp(String otp) {
        if (!Pattern.compile("^\\d{6}$").matcher(otp).matches()) {
            throw new EkycValidationException("Invalid OTP format");
        }
    }

    private EkycResponse handleFailedResponse(String referenceId) {
        return EkycResponse.builder()
            .status(VerificationStatus.FAILED)
            .referenceId(referenceId)
            .build();
    }
}
```