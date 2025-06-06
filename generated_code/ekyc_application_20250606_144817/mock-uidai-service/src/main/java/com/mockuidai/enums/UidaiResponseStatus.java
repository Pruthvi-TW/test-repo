package com.mockuidai.enums;

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
    INVALID_REQUEST
}