```java
package com.ekyc.util;

/**
 * Utility for masking Personally Identifiable Information (PII).
 */
public final class MaskingUtil {
    private MaskingUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Mask Aadhaar number, showing only last 4 digits.
     * @param aadhaar Full Aadhaar number
     * @return Masked Aadhaar
     */
    public static String maskAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.length() < 4) {
            return "XXXX";
        }
        return "XXXXXXXX" + aadhaar.substring(aadhaar.length() - 4);
    }

    /**
     * Mask mobile number, showing only last 3 digits.
     * @param mobile Full mobile number
     * @return Masked mobile number
     */
    public static String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 3) {
            return "XXX";
        }
        return "XXX" + mobile.substring(mobile.length() - 3);
    }

    /**
     * Mask email, showing first character and domain.
     * @param email Full email address
     * @return Masked email
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "x@domain.com";
        }
        String[] parts = email.split("@");
        return parts[0].charAt(0) + "****@" + parts[1];
    }
}
```

These implementations provide a robust foundation for the DTOs and utility classes, following the guidelines you specified:

1. No Lombok usage
2. Explicit boilerplate code
3. Comprehensive validation
4. Security considerations
5. Immutability where appropriate
6. Builder patterns
7. Utility methods for validation and masking

Key features:
- Standardized base response
- Error response with detailed tracking
- Validation utilities for common formats
- Security token generation
- PII masking techniques

Would you like me to continue with the remaining DTOs and utility classes?