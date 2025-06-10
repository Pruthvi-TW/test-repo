```java
package com.ekyc.util;

import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern AADHAAR_PATTERN = 
        Pattern.compile("^[2-9]{1}[0-9]{11}$");
    private static final Pattern VID_PATTERN = 
        Pattern.compile("^[2-9]{1}[0-9]{15}$");
    private static final Pattern MOBILE_PATTERN = 
        Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private ValidationUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isValidAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.trim().isEmpty()) {
            return false;
        }
        return AADHAAR_PATTERN.matcher(aadhaar).matches() && 
               verifyVerhoeffChecksum(aadhaar);
    }

    public static boolean isValidVID(String vid) {
        if (vid == null || vid.trim().isEmpty()) {
            return false;
        }
        return VID_PATTERN.matcher(vid).matches();
    }

    public static boolean isValidMobile(String mobile) {
        if (mobile == null || mobile.trim().isEmpty()) {
            return false;
        }
        return MOBILE_PATTERN.matcher(mobile).matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private static boolean verifyVerhoeffChecksum(String num) {
        // Implementation of Verhoeff algorithm for Aadhaar checksum
        int[][] d = new int[][]{
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 2, 3, 4, 0, 6, 7, 8, 9, 5},
            // ... rest of the Verhoeff tables
        };
        // Actual implementation here
        return true; // Placeholder
    }
}
```