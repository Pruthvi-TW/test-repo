package com.ekyc.enums;

/**
 * Enumeration of consent types required for eKYC verification.
 * These represent the different types of consent that users must provide
 * before their data can be processed.
 */
public enum ConsentType {
    
    /**
     * Consent to verify identity using provided ID
     */
    IDENTITY_VERIFICATION("Consent to verify identity using provided ID"),
    
    /**
     * Consent to access demographic data (name, address, etc.)
     */
    DEMOGRAPHIC_DATA("Consent to access demographic data (name, address, etc.)"),
    
    /**
     * Consent to access biometric data (fingerprint, iris, etc.)
     */
    BIOMETRIC_DATA("Consent to access biometric data (fingerprint, iris, etc.)"),
    
    /**
     * Consent to store data for the specified retention period
     */
    DATA_STORAGE("Consent to store data for the specified retention period"),
    
    /**
     * Consent to share data with third parties for verification purposes
     */
    DATA_SHARING("Consent to share data with third parties for verification purposes"),
    
    /**
     * Consent to receive communications related to the eKYC process
     */
    COMMUNICATIONS("Consent to receive communications related to the eKYC process"),
    
    /**
     * Consent to all terms and conditions of the eKYC process
     */
    TERMS_AND_CONDITIONS("Consent to all terms and conditions of the eKYC process");
    
    private final String description;
    
    ConsentType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if the consent type is mandatory for eKYC verification
     * 
     * @return true if the consent type is mandatory
     */
    public boolean isMandatory() {
        return this == IDENTITY_VERIFICATION || 
               this == DEMOGRAPHIC_DATA || 
               this == DATA_STORAGE || 
               this == TERMS_AND_CONDITIONS;
    }
    
    /**
     * Returns all mandatory consent types
     * 
     * @return Array of mandatory consent types
     */
    public static ConsentType[] getMandatoryConsents() {
        return new ConsentType[] {
            IDENTITY_VERIFICATION,
            DEMOGRAPHIC_DATA,
            DATA_STORAGE,
            TERMS_AND_CONDITIONS
        };
    }
}