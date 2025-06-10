```java
package com.ekyc.service;

import com.ekyc.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ValidationService {

    private static final Pattern IDENTITY_NUMBER_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern OTP_PATTERN = Pattern.compile("^\\d{6}$");

    public void validateIdentityNumber(String identityNumber, String idType) {
        if (identityNumber == null || !IDENTITY_NUMBER_PATTERN.matcher(identityNumber).matches()) {
            throw new ValidationException("Invalid Identity Number: Must be 12 numeric digits");
        }

        // Validate ID Type
        if (!"AADHAAR".equalsIgnoreCase(idType) && !"VID".equalsIgnoreCase(idType)) {
            throw new ValidationException("Unsupported ID Type: Must be AADHAAR or VID");
        }
    }

    public void validateConsent(boolean identityConsent, boolean contactConsent) {
        if (!identityConsent) {
            throw new ValidationException("Identity Verification Consent is Mandatory");
        }
    }

    public void validateOtp(String otp) {
        if (otp == null || !OTP_PATTERN.matcher(otp).matches()) {
            throw new ValidationException("Invalid OTP: Must be 6 numeric digits");
        }
    }
}
```

These implementations cover the core business flow you described, with key features:

1. Comprehensive input validation
2. Secure handling of sensitive data
3. Audit logging
4. Error management
5. Transactional processing
6. Separation of concerns

Key points in the implementation:
- Uses UUID for unique identifiers
- Implements masking for sensitive data
- Handles different status scenarios
- Provides detailed error handling
- Uses Spring's transaction management
- Implements logging with masked data

Recommended next steps:
1. Create corresponding DTOs (Data Transfer Objects)
2. Implement UidaiIntegrationService
3. Create repositories
4. Set up comprehensive exception handling
5. Add comprehensive unit and integration tests

Would you like me to elaborate on any specific aspect or generate the next set of files?