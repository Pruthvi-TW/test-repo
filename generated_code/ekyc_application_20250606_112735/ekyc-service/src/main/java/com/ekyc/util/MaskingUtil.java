```java
package com.ekyc.util;

import org.springframework.stereotype.Component;

@Component
public class MaskingUtil {

    private static final String MASKED_AADHAAR = "XXXXXXXX";
    private static final String MASKED_MOBILE = "XXXXXX";
    
    /**
     * Masks an Aadhaar number showing only last 4 digits
     * @param aadhaar The Aadhaar number to mask
     * @return Masked Aadhaar number
     */
    public String maskAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.length() != 12) {
            return MASKED_AADHAAR;
        }
        return "XXXX-XXXX-" + aadhaar.substring(8);
    }

    /**
     * Masks a mobile number showing only last 4 digits
     * @param mobile The mobile number to mask
     * @return Masked mobile number
     */
    public String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 4) {
            return MASKED_MOBILE;
        }
        return "XXXXXX" + mobile.substring(mobile.length() - 4);
    }

    /**
     * Masks an email address showing only first character and domain
     * @param email The email address to mask
     * @return Masked email address
     */
    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "X@X.com";
        }
        String[] parts = email.split("@");
        if (parts[0].length() > 1) {
            return parts[0].substring(0, 1) + "XXX@" + parts[1];
        }
        return "X@" + parts[1];
    }

    /**
     * Masks a name showing only first character of each word
     * @param name The name to mask
     * @return Masked name
     */
    public String maskName(String name) {
        if (name == null || name.isEmpty()) {
            return "XXX";
        }
        StringBuilder masked = new StringBuilder();
        String[] words = name.split("\\s+");
        for (String word : words) {
            if (!word.isEmpty()) {
                masked.append(word.charAt(0)).append("XXX ");
            }
        }
        return masked.toString().trim();
    }
}
```

Would you like me to continue with the remaining files? Each implementation follows the specified guidelines with:
- Comprehensive validation
- Security considerations
- PII data masking
- Proper error handling
- Complete documentation
- Immutable where appropriate
- Builder patterns for complex objects

Let me know if you want to see any specific file next or have any questions about the implementations shown so far.