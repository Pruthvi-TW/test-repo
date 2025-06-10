```java
package com.ekyc.enums;

/**
 * Standardized error codes for the eKYC service.
 */
public enum ErrorCode {
    // Validation Errors
    INVALID_INPUT("E001", "Invalid input data"),
    MISSING_REQUIRED_FIELD("E002", "Missing required field"),
    INVALID_FORMAT("E003", "Invalid data format"),

    // Authentication Errors
    UNAUTHORIZED("E101", "Unauthorized access"),
    AUTHENTICATION_FAILED("E102", "Authentication failed"),
    OTP_VERIFICATION_FAILED("E103", "OTP verification failed"),

    // Business Logic Errors
    DUPLICATE_REQUEST("E201", "Duplicate request detected"),
    INVALID_STATUS_TRANSITION("E202", "Invalid status transition"),
    PROCESSING_ERROR("E203", "Processing error occurred"),

    // External Integration Errors
    EXTERNAL_SERVICE_UNAVAILABLE("E301", "External service unavailable"),
    INTEGRATION_TIMEOUT("E302", "Integration request timed out"),

    // System Errors
    INTERNAL_SERVER_ERROR("E500", "Internal server error"),
    DATABASE_ERROR("E501", "Database operation failed"),
    CONFIGURATION_ERROR("E502", "System configuration error");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
```

These files provide a robust foundation for error handling and response management in the eKYC service. Key features include:

1. `BaseResponse`: 
   - Unique transaction reference
   - Timestamp tracking
   - Success flag
   - Builder pattern for flexible object creation

2. `ErrorResponse`:
   - Extends `BaseResponse`
   - Standardized error code and message
   - Technical details for debugging
   - Always sets success to false

3. `ErrorCode` Enum:
   - Comprehensive error categories
   - Unique error codes
   - Descriptive error messages

Would you like me to continue with the other DTOs and utilities?