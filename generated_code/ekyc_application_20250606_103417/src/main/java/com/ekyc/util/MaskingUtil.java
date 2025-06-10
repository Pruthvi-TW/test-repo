```java
package com.ekyc.util;

import java.util.regex.Pattern;

public final class MaskingUtil {
    
    private static final Pattern ID_PATTERN = Pattern.compile("\\b[A-Z0-9]{8,15}\\b");
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("\\b\\d{10,12}\\b");

    private MaskingUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String maskSensitiveData(String input) {
        if (input == null) return null;
        
        String masked = maskIdNumber(input);
        masked = maskEmail(masked);
        masked = maskPhoneNumber(masked);
        
        return masked;
    }

    private static String maskIdNumber(String input) {
        return ID_PATTERN.matcher(input).replaceAll(match -> 
            maskMiddleChars(match.group(), 4, 4));
    }

    private static String maskEmail(String input) {
        return EMAIL_PATTERN.matcher(input).replaceAll(match -> {
            String email = match.group();
            int atIndex = email.indexOf('@');
            return maskMiddleChars(email.substring(0, atIndex), 2, 1) + 
                   email.substring(atIndex);
        });
    }

    private static String maskPhoneNumber(String input) {
        return PHONE_PATTERN.matcher(input).replaceAll(match -> 
            maskMiddleChars(match.group(), 2, 2));
    }

    private static String maskMiddleChars(String input, int prefixLength, 
                                        int suffixLength) {
        if (input == null || input.length() <= prefixLength + suffixLength) {
            return input;
        }

        String prefix = input.substring(0, prefixLength);
        String suffix = input.substring(input.length() - suffixLength);
        String masked = "X".repeat(input.length() - prefixLength - suffixLength);
        
        return prefix + masked + suffix;
    }
}
```

I'll continue with the remaining files in the next response, including:
- OTP verification DTOs
- ValidationUtil
- Custom exceptions
- Global exception handler
- Additional enums

Would you like me to proceed with those files?