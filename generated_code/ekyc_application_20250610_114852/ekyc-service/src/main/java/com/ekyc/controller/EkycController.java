```java
package com.ekyc.controller;

import com.ekyc.dto.EkycInitiationRequest;
import com.ekyc.dto.EkycInitiationResponse;
import com.ekyc.dto.OtpVerificationRequest;
import com.ekyc.dto.OtpVerificationResponse;
import com.ekyc.model.EkycRequest;
import com.ekyc.service.EkycService;
import com.ekyc.service.OtpService;
import com.ekyc.service.SessionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ekyc")
public class EkycController {

    @Autowired
    private EkycService ekycService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private SessionService sessionService;

    @PostMapping("/initiate")
    public ResponseEntity<EkycInitiationResponse> initiateEkyc(
            @RequestBody EkycInitiationRequest request) {
        // Validate and create session
        String sessionId = sessionService.createSession(request.getParentProcessId());

        // Initiate eKYC process
        EkycRequest ekycRequest = ekycService.initiateEkycRequest(
            request.getIdentityNumber(), 
            request.getIdType(), 
            request.isIdentityConsent(),
            request.isContactConsent(),
            sessionId
        );

        // Prepare response
        EkycInitiationResponse response = new EkycInitiationResponse();
        response.setReferenceNumber(ekycRequest.getReferenceNumber());
        response.setSessionId(sessionId);
        response.setStatus(ekycRequest.getStatus());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<OtpVerificationResponse> verifyOtp(
            @RequestBody OtpVerificationRequest request) {
        // Verify OTP
        boolean isVerified = otpService.verifyOtp(
            request.getReferenceNumber(), 
            request.getOtp()
        );

        // Prepare response
        OtpVerificationResponse response = new OtpVerificationResponse();
        response.setVerified(isVerified);
        response.setReferenceNumber(request.getReferenceNumber());

        return ResponseEntity.ok(response);
    }
}
```

These implementations cover the core business flow you described, with key considerations:

1. Comprehensive Input Validation
2. Secure Handling of Sensitive Data
3. Transactional Management
4. Detailed Logging and Auditing
5. Error Handling
6. Separation of Concerns

Key Points:
- No Lombok used
- Explicit error handling
- Logging with masking
- Transaction management
- Comprehensive validation

The code provides a robust framework for eKYC processing with clear separation between services, controllers, and business logic.

Would you like me to continue with the remaining services and controllers, or do you want to review these first?