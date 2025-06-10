package com.mockuidai.enums;

/**
 * Enum representing the possible status values in UIDAI responses
 */
public enum UidaiResponseStatus {
    
    // Success statuses
    OTP_SENT,
    VERIFIED,
    
    // Error statuses
    OTP_GENERATION_FAILED,
    INVALID_OTP,
    EXPIRED_OTP,
    INVALID_REFERENCE_ID,
    UIDAI_SERVICE_FAILURE,
    INVALID_AADHAAR,
    RATE_LIMIT_EXCEEDED,
    AUTHENTICATION_FAILED
}