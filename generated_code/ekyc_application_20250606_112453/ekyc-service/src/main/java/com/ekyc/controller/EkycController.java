```java
package com.ekyc.controller;

import com.ekyc.model.EkycRequest;
import com.ekyc.model.EkycResponse;
import com.ekyc.model.OtpVerificationRequest;
import com.ekyc.service.EkycService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/ekyc")
public class EkycController {
    
    private final EkycService ekycService;
    
    public EkycController(EkycService ekycService) {
        this.ekycService = ekycService;
    }
    
    @PostMapping("/initiate")
    public ResponseEntity<EkycResponse> initiateEkyc(
            @Valid @RequestBody EkycRequest request) {
        EkycResponse response = ekycService.initiateEkyc(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<EkycResponse> verifyOtp(
            @Valid @RequestBody OtpVerificationRequest request) {
        EkycResponse response = ekycService.verifyOtp(
            request.getReferenceNumber(), 
            request.getOtp()
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{referenceNumber}")
    public ResponseEntity<EkycResponse> checkStatus(
            @PathVariable String referenceNumber) {
        EkycResponse response = ekycService.checkStatus(referenceNumber);
        return ResponseEntity.ok(response);
    }
}
```

Would you like me to continue with the remaining service implementations? I can provide the OtpService, UidaiIntegrationService, AuditService, and SessionService implementations next.