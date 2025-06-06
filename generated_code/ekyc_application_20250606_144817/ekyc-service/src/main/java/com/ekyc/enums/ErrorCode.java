package com.ekyc.enums;

/**
 * Enumeration of error codes used in the eKYC service.
 * Each error code has a unique code, a message, and an HTTP status code.
 */
public enum ErrorCode {
    
    // General errors (1000-1999)
    GENERAL_ERROR("ERR1000", "An unexpected error occurred", 500),
    INVALID_REQUEST("ERR1001", "Invalid request format", 400),
    MISSING_REQUIRED_FIELD("ERR1002", "Required field is missing", 400),
    INVALID_FIELD_FORMAT("ERR1003", "Field format is invalid", 400),
    UNAUTHORIZED_ACCESS("ERR1004", "Unauthorized access", 401),
    FORBIDDEN_ACCESS("ERR1005", "Forbidden access", 403),
    RESOURCE_NOT_FOUND("ERR1006", "Resource not found", 404),
    REQUEST_TIMEOUT("ERR1007", "Request timed out", 408),
    TOO_MANY_REQUESTS("ERR1008", "Too many requests", 429),
    
    // Authentication errors (2000-2999)
    AUTHENTICATION_FAILED("ERR2000", "Authentication failed", 401),
    INVALID_CREDENTIALS("ERR2001", "Invalid credentials", 401),
    ACCOUNT_LOCKED("ERR2002", "Account is locked", 403),
    SESSION_EXPIRED("ERR2003", "Session has expired", 401),
    INVALID_TOKEN("ERR2004", "Invalid token", 401),
    TOKEN_EXPIRED("ERR2005", "Token has expired", 401),
    
    // eKYC specific errors (3000-3999)
    INVALID_ID_TYPE("ERR3000", "Invalid ID type", 400),
    INVALID_ID_FORMAT("ERR3001", "Invalid ID format", 400),
    INVALID_MOBILE_NUMBER("ERR3002", "Invalid mobile number", 400),
    INVALID_EMAIL("ERR3003", "Invalid email address", 400),
    INVALID_NAME("ERR3004", "Invalid name format", 400),
    INVALID_DOB("ERR3005", "Invalid date of birth", 400),
    INVALID_GENDER("ERR3006", "Invalid gender", 400),
    INVALID_ADDRESS("ERR3007", "Invalid address", 400),
    INVALID_PINCODE("ERR3008", "Invalid pincode", 400),
    
    // OTP errors (4000-4999)
    OTP_GENERATION_FAILED("ERR4000", "OTP generation failed", 500),
    OTP_DELIVERY_FAILED("ERR4001", "OTP delivery failed", 500),
    INVALID_OTP("ERR4002", "Invalid OTP", 400),
    OTP_EXPIRED("ERR4003", "OTP has expired", 400),
    OTP_ATTEMPTS_EXCEEDED("ERR4004", "Maximum OTP attempts exceeded", 400),
    OTP_ALREADY_VERIFIED("ERR4005", "OTP already verified", 400),
    
    // Verification errors (5000-5999)
    VERIFICATION_FAILED("ERR5000", "Verification failed", 400),
    ID_NOT_FOUND("ERR5001", "ID not found", 404),
    ID_EXPIRED("ERR5002", "ID has expired", 400),
    ID_BLOCKED("ERR5003", "ID is blocked", 400),
    PHOTO_MISMATCH("ERR5004", "Photo verification failed", 400),
    BIOMETRIC_MISMATCH("ERR5005", "Biometric verification failed", 400),
    DEMOGRAPHIC_MISMATCH("ERR5006", "Demographic data mismatch", 400),
    
    // Consent errors (6000-6999)
    CONSENT_REQUIRED("ERR6000", "Consent is required", 400),
    INVALID_CONSENT("ERR6001", "Invalid consent", 400),
    CONSENT_EXPIRED("ERR6002", "Consent has expired", 400),
    
    // External service errors (7000-7999)
    EXTERNAL_SERVICE_UNAVAILABLE("ERR7000", "External service is unavailable", 503),
    EXTERNAL_SERVICE_TIMEOUT("ERR7001", "External service timed out", 504),
    EXTERNAL_SERVICE_ERROR("ERR7002", "External service returned an error", 502),
    
    // Data errors (8000-8999)
    DATA_NOT_FOUND("ERR8000", "Data not found", 404),
    DATA_ALREADY_EXISTS("ERR8001", "Data already exists", 409),
    DATA_VALIDATION_FAILED("ERR8002", "Data validation failed", 400),
    DATA_INTEGRITY_VIOLATION("ERR8003", "Data integrity violation", 400),
    
    // Transaction errors (9000-9999)
    TRANSACTION_FAILED("ERR9000", "Transaction failed", 500),
    TRANSACTION_TIMEOUT("ERR9001", "Transaction timed out", 408),
    TRANSACTION_ALREADY_PROCESSED("ERR9002", "Transaction already processed", 409),
    TRANSACTION_NOT_FOUND("ERR9003", "Transaction not found", 404),
    TRANSACTION_EXPIRED("ERR9004", "Transaction has expired", 400);
    
    private final String code;
    private final String message;
    private final int httpStatus;
    
    ErrorCode(String code, String message, int httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public int getHttpStatus() {
        return httpStatus;
    }
    
    /**
     * Returns the error code for the given code string
     * 
     * @param code The error code string to look up
     * @return The matching error code, or GENERAL_ERROR if not found
     */
    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return GENERAL_ERROR;
    }
    
    /**
     * Checks if the error is a client error (4xx)
     * 
     * @return true if the error is a client error
     */
    public boolean isClientError() {
        return httpStatus >= 400 && httpStatus < 500;
    }
    
    /**
     * Checks if the error is a server error (5xx)
     * 
     * @return true if the error is a server error
     */
    public boolean isServerError() {
        return httpStatus >= 500;
    }
}