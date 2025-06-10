```java
package com.ekyc.util;

/**
 * Utility class for masking Personally Identifiable Information (PII).
 */
public final class MaskingUtil {
    private MaskingUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Mask Aadhaar number, showing only last 4 digits.
     * @param aadhaar Full Aadhaar number
     * @return Masked Aadhaar number
     */
    public static String maskAadhaar(String aadhaar) {
        if (ValidationUtil.isNullOrEmpty(aadhaar) || aadhaar.length() < 4) {
            return aadhaar;
        }
        return "XXXX-XXXX-" + aadhaar.substring(aadhaar.length() - 4);
    }

    /**
     * Mask mobile number, showing only last 4 digits.
     * @param mobile Full mobile number
     * @return Masked mobile number
     */
    public static String maskMobile(String mobile) {
        if (ValidationUtil.isNullOrEmpty(mobile) || mobile.length() < 4) {
            return mobile;
        }
        return "XXXX-" + mobile.substring(mobile.length() - 4);
    }

    /**
     * Mask email address, showing only domain.
     * @param email Full email address
     * @return Masked email address
     */
    public static String maskEmail(String email) {
        if (ValidationUtil.isNullOrEmpty(email) || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        return "XXXX@" + parts[1];
    }

    /**
     * Mask name by showing only first and last characters.
     * @param name Full name
     * @return Masked name
     */
    public static String maskName(String name) {
        if (ValidationUtil.isNullOrEmpty(name) || name.length() <= 2) {
            return name;
        }
        return name.charAt(0) + "XXX" + name.charAt(name.length() - 1);
    }
}
```

These files provide a robust foundation for the DTOs and utility classes, following the guidelines you specified:

1. No Lombok usage
2. Explicit boilerplate code
3. Comprehensive validation
4. Immutability considerations
5. Standardized error handling
6. Utility methods for validation and masking

Key features:
- BaseResponse for consistent response tracking
- ErrorResponse with detailed error information
- Standardized ErrorCode enum
- ValidationUtil with regex-based validations
- MaskingUtil for secure PII handling

Would you like me to continue with the remaining DTOs and utility classes?