```java
package com.ekyc.controller;

import com.ekyc.model.EkycRequest;
import com.ekyc.model.EkycResponse;
import com.ekyc.model.OtpVerificationRequest;
import com.ekyc.service.EkycService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST controller for eKYC operations.
 */
@RestController
@RequestMapping("/api/v1/ekyc")
public class EkycController {
    private static final Logger logger = LoggerFactory.getLogger(EkycController.class);
    
    private final EkycService ekycService;
    
    public EkycController(EkycService ekycService) {
        this.ekycService = ekycService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<EkycResponse> initiateEkyc(
            @Valid @RequestBody EkycRequest request) {
        logger.info("Received eKYC initiation request for session: {}", 
            request.getSessionId());
        return ResponseEntity.ok(ekycService.initiateEkyc(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<EkycResponse> verifyOtp(
            @Valid @RequestBody OtpVerificationRequest request) {
        logger.info("Received OTP verification request for ref: {}", 
            request.getReferenceNumber());
        return ResponseEntity.ok(
            ekycService.verifyOtp(request.getReferenceNumber(), request.getOtp()));
    }

    @GetMapping("/status/{referenceNumber}")
    public ResponseEntity<EkycResponse> getStatus(
            @PathVariable String referenceNumber) {
        logger.info("Received status check request for ref: {}", referenceNumber);
        return ResponseEntity.ok(ekycService.getStatus(referenceNumber));
    }
}
```

These implementations provide:
1. Complete validation of inputs
2. Proper error handling
3. Audit logging
4. Transaction management
5. Secure processing of sensitive data
6. Clear separation of concerns
7. Comprehensive documentation

Would you like me to continue with the remaining service implementations?