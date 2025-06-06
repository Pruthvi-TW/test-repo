package com.ekyc.enums;

/**
 * Enumeration of audit actions for tracking user and system activities.
 * Used for comprehensive audit logging throughout the eKYC process.
 */
public enum AuditAction {
    
    // User-initiated actions
    USER_LOGIN("User login attempt"),
    USER_LOGOUT("User logout"),
    PASSWORD_CHANGE("Password change"),
    PASSWORD_RESET("Password reset request"),
    
    // eKYC process actions
    EKYC_INITIATED("eKYC process initiated"),
    EKYC_OTP_REQUESTED("OTP requested for eKYC"),
    EKYC_OTP_VERIFIED("OTP verified for eKYC"),
    EKYC_OTP_FAILED("OTP verification failed"),
    EKYC_VERIFICATION_STARTED("eKYC verification started"),
    EKYC_VERIFICATION_COMPLETED("eKYC verification completed"),
    EKYC_VERIFICATION_FAILED("eKYC verification failed"),
    EKYC_CANCELLED("eKYC process cancelled"),
    
    // Consent actions
    CONSENT_PROVIDED("Consent provided"),
    CONSENT_WITHDRAWN("Consent withdrawn"),
    CONSENT_UPDATED("Consent updated"),
    
    // Data access actions
    DATA_ACCESSED("Data accessed"),
    DATA_CREATED("Data created"),
    DATA_UPDATED("Data updated"),
    DATA_DELETED("Data deleted"),
    DATA_EXPORTED("Data exported"),
    
    // Admin actions
    USER_CREATED("User created"),
    USER_UPDATED("User updated"),
    USER_DELETED("User deleted"),
    USER_BLOCKED("User blocked"),
    USER_UNBLOCKED("User unblocked"),
    ROLE_ASSIGNED("Role assigned to user"),
    ROLE_REVOKED("Role revoked from user"),
    
    // System actions
    SYSTEM_ERROR("System error occurred"),
    SYSTEM_WARNING("System warning occurred"),
    SYSTEM_STARTUP("System startup"),
    SYSTEM_SHUTDOWN("System shutdown"),
    SCHEDULED_JOB_STARTED("Scheduled job started"),
    SCHEDULED_JOB_COMPLETED("Scheduled job completed"),
    SCHEDULED_JOB_FAILED("Scheduled job failed"),
    
    // Integration actions
    EXTERNAL_API_CALL("External API call made"),
    EXTERNAL_API_RESPONSE("External API response received"),
    EXTERNAL_API_ERROR("External API error occurred"),
    
    // Security actions
    SUSPICIOUS_ACTIVITY("Suspicious activity detected"),
    BRUTE_FORCE_ATTEMPT("Brute force attempt detected"),
    ACCESS_DENIED("Access denied"),
    PERMISSION_VIOLATION("Permission violation attempt");
    
    private final String description;
    
    AuditAction(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Determines if this action requires detailed logging with full context
     * 
     * @return true if the action requires detailed logging
     */
    public boolean requiresDetailedLogging() {
        return this == EKYC_INITIATED || 
               this == EKYC_VERIFICATION_COMPLETED || 
               this == EKYC_VERIFICATION_FAILED ||
               this == SUSPICIOUS_ACTIVITY ||
               this == BRUTE_FORCE_ATTEMPT ||
               this == ACCESS_DENIED ||
               this == PERMISSION_VIOLATION;
    }
    
    /**
     * Determines if this action is security-sensitive
     * 
     * @return true if the action is security-sensitive
     */
    public boolean isSecuritySensitive() {
        return this == USER_LOGIN ||
               this == PASSWORD_CHANGE ||
               this == PASSWORD_RESET ||
               this == SUSPICIOUS_ACTIVITY ||
               this == BRUTE_FORCE_ATTEMPT ||
               this == ACCESS_DENIED ||
               this == PERMISSION_VIOLATION;
    }
    
    /**
     * Determines if this action is related to personal data
     * 
     * @return true if the action is related to personal data
     */
    public boolean involvesPersonalData() {
        return this == EKYC_INITIATED ||
               this == EKYC_VERIFICATION_COMPLETED ||
               this == DATA_ACCESSED ||
               this == DATA_CREATED ||
               this == DATA_UPDATED ||
               this == DATA_DELETED ||
               this == DATA_EXPORTED;
    }
}