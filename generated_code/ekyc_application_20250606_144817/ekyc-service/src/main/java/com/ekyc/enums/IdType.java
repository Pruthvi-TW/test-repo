package com.ekyc.enums;

/**
 * Enumeration of supported identification types for eKYC verification.
 */
public enum IdType {
    
    /**
     * Aadhaar number (12-digit unique identification number)
     */
    AADHAAR("Aadhaar", "^[2-9]{1}[0-9]{11}$"),
    
    /**
     * Virtual ID (16-digit temporary identification number linked to Aadhaar)
     */
    VID("Virtual ID", "^[2-9]{1}[0-9]{15}$"),
    
    /**
     * PAN (Permanent Account Number - 10-character alphanumeric identifier)
     */
    PAN("PAN", "^[A-Z]{5}[0-9]{4}[A-Z]{1}$"),
    
    /**
     * Driving License number
     */
    DRIVING_LICENSE("Driving License", "^(([A-Z]{2}[0-9]{2})( )|([A-Z]{2}-[0-9]{2}))((19|20)[0-9][0-9])[0-9]{7}$"),
    
    /**
     * Voter ID card number
     */
    VOTER_ID("Voter ID", "^[A-Z]{3}[0-9]{7}$"),
    
    /**
     * Passport number
     */
    PASSPORT("Passport", "^[A-Z]{1}[0-9]{7}$");
    
    private final String displayName;
    private final String validationPattern;
    
    IdType(String displayName, String validationPattern) {
        this.displayName = displayName;
        this.validationPattern = validationPattern;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getValidationPattern() {
        return validationPattern;
    }
    
    /**
     * Checks if the provided ID value matches the validation pattern for this ID type
     * 
     * @param idValue The ID value to validate
     * @return true if the ID value is valid for this ID type
     */
    public boolean isValid(String idValue) {
        if (idValue == null || idValue.isEmpty()) {
            return false;
        }
        return idValue.matches(validationPattern);
    }
    
    /**
     * Returns the ID type for the given display name
     * 
     * @param displayName The display name to look up
     * @return The matching ID type, or null if not found
     */
    public static IdType fromDisplayName(String displayName) {
        for (IdType idType : values()) {
            if (idType.getDisplayName().equalsIgnoreCase(displayName)) {
                return idType;
            }
        }
        return null;
    }
}