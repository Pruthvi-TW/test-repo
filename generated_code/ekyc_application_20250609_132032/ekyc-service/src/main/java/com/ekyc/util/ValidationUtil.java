```java
package com.ekyc.util;

import java.util.regex.Pattern;

/**
 * Utility class for comprehensive validation of various input types.
 */
public final class ValidationUtil {
    // Private constructor to prevent instantiation
    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Aadhaar number validation regex
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("^[2-9]{1}[0-9]{11}$");

    // VID validation regex
    private static final Pattern VID_PATTERN = Pattern.compile("^[1-9][0-9]{15}$");

    /**
     * Validate Aadhaar number format.
     * @param aadhaar Aadhaar number to validate
     * @return boolean indicating validity
     */
    public static boolean isValidAadhaar(String aadhaar) {
        if (aadhaar == null) return false;
        return AADHAAR_PATTERN.matcher(aadhaar.replaceAll("\\s", "")).matches();
    }

    /**
     * Validate VID format.
     * @param vid Virtual ID to validate
     * @return boolean indicating validity
     */
    public static boolean isValidVid(String vid) {
        if (vid == null) return false;
        return VID_PATTERN.matcher(vid.replaceAll("\\s", "")).matches();
    }

    /**
     * Validate mobile number format.
     * @param mobileNumber Mobile number to validate
     * @return boolean indicating validity
     */
    public static boolean isValidMobileNumber(String mobileNumber) {
        if (mobileNumber == null) return false;
        return Pattern.matches("^[6-9]\\d{9}$", mobileNumber);
    }

    /**
     * Validate email format.
     * @param email Email to validate
     * @return boolean indicating validity
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email);
    }
}
```