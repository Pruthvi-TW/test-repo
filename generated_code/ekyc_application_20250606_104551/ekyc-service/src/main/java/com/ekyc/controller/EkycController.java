```java
package com.ekyc.controller;

import com.ekyc.model.EkycRequest;
import com.ekyc.model.EkycResponse;
import com.ekyc.model.OtpVerificationRequest;
import com.ekyc.model.OtpVerificationResponse;
import com.ekyc.service.EkycService;
import com.ekyc.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/ekyc")
@RequiredArgsConstructor
public class EkycController {

    private final EkycService ekycService;
    private final OtpService otpService;

    @Operation(summary = "Initiate eKYC verification")
    @PostMapping("/initiate")
    public ResponseEntity<EkycResponse> initiateEkyc(
            @Validated @RequestBody EkycRequest request) {
        log.info("Received eKYC initiation request for session: {}", 
            request.getSessionId());
        return ResponseEntity.ok(ekycService.initiateEkyc(request));
    }

    @Operation(summary = "Verify OTP")
    @PostMapping("/verify-otp")
    public ResponseEntity<OtpVerificationResponse> verifyOtp(
            @Validated @RequestBody OtpVerificationRequest request) {
        log.info("Received OTP verification request for reference: {}", 
            request.getReferenceNumber());
        return ResponseEntity.ok(otpService.verifyOtp(request));
    }

    @Operation(summary = "Check eKYC status")
    @GetMapping("/status/{referenceNumber}")
    public ResponseEntity<EkycResponse> checkStatus(
            @PathVariable String referenceNumber) {
        log.info("Checking status for reference: {}", referenceNumber);
        return ResponseEntity.ok(ekycService.checkStatus(referenceNumber));
    }
}
```

This is a partial implementation focusing on the core services and controller. Would you like me to continue with the remaining files including the integration service, audit service, and exception handlers?