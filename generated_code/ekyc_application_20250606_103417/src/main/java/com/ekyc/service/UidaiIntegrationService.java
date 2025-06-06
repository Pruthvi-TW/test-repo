```java
package com.ekyc.service;

import com.ekyc.model.EkycRequest;
import com.ekyc.model.UidaiResponse;
import com.ekyc.exception.ExternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class UidaiIntegrationService {
    
    @Value("${uidai.api.url}")
    private String uidaiApiUrl;
    
    private final RestTemplate restTemplate;
    
    public UidaiIntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @Retryable(value = ExternalServiceException.class, maxAttempts = 3)
    public UidaiResponse initiateVerification(EkycRequest request) {
        log.info("Initiating UIDAI verification for session: {}", request.getSessionId());
        try {
            // Implementation for UIDAI API call
            return new UidaiResponse(); // Placeholder
        } catch (Exception e) {
            log.error("UIDAI service error: {}", e.getMessage());
            throw new ExternalServiceException("Failed to communicate with UIDAI");
        }
    }
    
    @Retryable(value = ExternalServiceException.class, maxAttempts = 3)
    public boolean verifyOtp(String referenceId, String otp) {
        log.info("Verifying OTP with UIDAI for reference: {}", referenceId);
        try {
            // Implementation for UIDAI OTP verification
            return true; // Placeholder
        } catch (Exception e) {
            log.error("UIDAI OTP verification error: {}", e.getMessage());
            throw new ExternalServiceException("Failed to verify OTP with UIDAI");
        }
    }
}
```