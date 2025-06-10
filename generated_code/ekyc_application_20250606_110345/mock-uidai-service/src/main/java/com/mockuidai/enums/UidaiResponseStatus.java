```java
package com.mockuidai.enums;

public enum UidaiResponseStatus {
    OTP_SENT,
    OTP_GENERATION_FAILED,
    VERIFIED,
    INVALID_OTP,
    EXPIRED_OTP,
    UIDAI_SERVICE_FAILURE
}