```java
package com.ekyc.enums;

/**
 * Standardized error codes for consistent error handling across services.
 */
public enum ErrorCode {
    // Validation Errors
    INVALID_INPUT("E001", "Invalid input data"),
    MISSING_REQUIRED_FIELD("E002", "Missing required field"),
    INVALID_FORMAT("E003", "Invalid data format"),

    // Authentication Errors
    UNAUTHORIZED("A001", "Unauthorized access"),
    AUTHENTICATION_FAILED("A002", "Authentication failed"),
    OTP_EXPIRED("A003", "OTP has expired"),

    // System Errors
    INTERNAL_SERVER_ERROR("S001", "Internal server error"),
    SERVICE_UNAVAILABLE("S002", "Service temporarily unavailable"),
    DATABASE_ERROR("S003", "Database operation failed"),

    // Business Logic Errors
    DUPLICATE_REQUEST("B001", "Duplicate request detected"),
    INVALID_STATE("B002", "Invalid request state"),
    VERIFICATION_FAILED("B003", "Verification process failed"),

    // Integration Errors
    EXTERNAL_SERVICE_ERROR("I001", "External service integration failed"),
    TIMEOUT("I002", "Request timed out"),
    RATE_LIMIT_EXCEEDED("I003", "Rate limit exceeded");

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