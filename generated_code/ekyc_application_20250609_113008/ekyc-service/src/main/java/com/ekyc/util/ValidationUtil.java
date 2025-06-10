```java
package com.ekyc.util;

import java.util.regex.Pattern;

/**
 * Utility class for comprehensive validation of various input types.
 */
public final class ValidationUtil {
    // Regex patterns for validation
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private ValidationUtil() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Validate Aadhaar number format.
     * @param aadhaar Aadhaar number to validate
     * @return boolean indicating validity
     */
    public static boolean isValidAadhaar(String aadhaar) {
        return aadhaar != null && AADHAAR_PATTERN.matcher(aadhaar).matches();
    }

    /**
     * Validate mobile number format.
     * @param mobile Mobile number to validate
     * @return boolean indicating validity
     */
    public static boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }

    /**
     * Validate email format.
     * @param email Email to validate
     * @return boolean indicating validity
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate not null and not empty.
     * @param value String to validate
     * @return boolean indicating validity
     */
    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
```