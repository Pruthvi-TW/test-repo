```java
package com.ekyc.dto.response;

import com.ekyc.enums.EkycStatus;
import java.time.LocalDateTime;
import java.util.Objects;

public final class EkycInitiateResponse {
    private final String referenceId;
    private final EkycStatus status;
    private final String maskedMobile;
    private final LocalDateTime timestamp;
    private final Integer otpExpiryMinutes;

    private EkycInitiateResponse(Builder builder) {
        this.referenceId = builder.referenceId;
        this.status = builder.status;
        this.maskedMobile = builder.maskedMobile;
        this.timestamp = builder.timestamp;
        this.otpExpiryMinutes = builder.otpExpiryMinutes;
    }

    // Getters only - immutable
    public String getReferenceId() { return referenceId; }
    public EkycStatus getStatus() { return status; }
    public String getMaskedMobile() { return maskedMobile; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Integer getOtpExpiryMinutes() { return otpExpiryMinutes; }

    public static class Builder {
        private String referenceId;
        private EkycStatus status;
        private String maskedMobile;
        private LocalDateTime timestamp;
        private Integer otpExpiryMinutes;

        public Builder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public Builder status(EkycStatus status) {
            this.status = status;
            return this;
        }

        public Builder maskedMobile(String maskedMobile) {
            this.maskedMobile = maskedMobile;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder otpExpiryMinutes(Integer otpExpiryMinutes) {
            this.otpExpiryMinutes = otpExpiryMinutes;
            return this;
        }

        public EkycInitiateResponse build() {
            return new EkycInitiateResponse(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EkycInitiateResponse)) return false;
        EkycInitiateResponse that = (EkycInitiateResponse) o;
        return Objects.equals(referenceId, that.referenceId) &&
                status == that.status &&
                Objects.equals(maskedMobile, that.maskedMobile) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(otpExpiryMinutes, that.otpExpiryMinutes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceId, status, maskedMobile, timestamp, otpExpiryMinutes);
    }
}
```

I'll continue with the remaining files in the next response due to length limitations. Would you like me to proceed with the other files?