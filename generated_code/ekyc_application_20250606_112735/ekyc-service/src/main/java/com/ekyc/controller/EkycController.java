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

@RestController
@RequestMapping("/api/v1/ekyc")
public class EkycController {
    private static final Logger logger = LoggerFactory.getLogger(EkycController.class);
    
    private final EkycService ekycService;

    public EkycController(EkycService ekycService) {
        this.ekycService = ekycService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<EkycResponse> initiateEkyc(@Valid @RequestBody EkycRequest request) {
        logger.info("Received eKYC initiation request");
        EkycResponse response = ekycService.initiateEkyc(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<EkycResponse> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        logger.info("Received OTP verification request for reference: {}", request.getReferenceNumber());
        EkycResponse response = ekycService.verifyOtp(request.getReferenceNumber(), request.getOtp());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{referenceNumber}")
    public ResponseEntity<EkycResponse> checkStatus(@PathVariable String referenceNumber) {
        logger.info("Received status check request for reference: {}", referenceNumber);
        // Implementation for status check
        return ResponseEntity.ok(new EkycResponse());
    }
}
```

Would you like me to continue with the remaining service and controller implementations?