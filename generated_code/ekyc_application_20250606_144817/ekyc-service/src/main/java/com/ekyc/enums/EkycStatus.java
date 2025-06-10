package com.ekyc.enums;

/**
 * Enumeration of possible eKYC process statuses.
 * Used to track the state of an eKYC verification flow.
 */
public enum EkycStatus {
    
    /**
     * eKYC process has been initiated but not completed
     */
    INITIATED("The eKYC process has been initiated"),
    
    /**
     * OTP has been sent to the user
     */
    OTP_SENT("OTP has been sent to the registered mobile number"),
    
    /**
     * OTP verification is pending
     */
    OTP_PENDING("OTP verification is pending"),
    
    /**
     * OTP has been verified successfully
     */
    OTP_VERIFIED("OTP has been verified successfully"),
    
    /**
     * OTP verification has failed
     */
    OTP_FAILED("OTP verification failed"),
    
    /**
     * eKYC verification is in progress
     */
    VERIFICATION_IN_PROGRESS("eKYC verification is in progress"),
    
    /**
     * eKYC verification has been completed successfully
     */
    COMPLETED("eKYC verification completed successfully"),
    
    /**
     * eKYC verification has failed
     */
    FAILED("eKYC verification failed"),
    
    /**
     * eKYC process has been rejected
     */
    REJECTED("eKYC verification was rejected"),
    
    /**
     * eKYC process has expired
     */
    EXPIRED("eKYC verification request has expired"),
    
    /**
     * eKYC process has been cancelled by the user
     */
    CANCELLED("eKYC verification was cancelled by the user");
    
    private final String description;
    
    EkycStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if the current status is a terminal status
     * 
     * @return true if the status is terminal (completed, failed, rejected, expired, cancelled)
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == REJECTED || 
               this == EXPIRED || this == CANCELLED;
    }
    
    /**
     * Checks if the current status is a success status
     * 
     * @return true if the status indicates success (completed)
     */
    public boolean isSuccess() {
        return this == COMPLETED;
    }
    
    /**
     * Checks if the current status is a failure status
     * 
     * @return true if the status indicates failure (failed, rejected, expired, cancelled)
     */
    public boolean isFailure() {
        return this == FAILED || this == REJECTED || this == EXPIRED || this == CANCELLED;
    }
}