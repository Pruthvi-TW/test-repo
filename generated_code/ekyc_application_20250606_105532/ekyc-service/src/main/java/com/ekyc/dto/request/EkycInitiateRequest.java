```java
package com.ekyc.dto.request;

import com.ekyc.enums.IdType;
import com.ekyc.enums.ConsentType;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

public final class EkycInitiateRequest {
    @NotNull(message = "ID number is required")
    @Pattern(regexp = "^[0-9]{12}$|^[0-9]{16}$", message = "Invalid ID format")
    private final String idNumber;

    @NotNull(message = "ID type is required")
    private final IdType idType;

    @NotNull(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    private final String mobileNumber;

    @NotNull(message = "Consent is required")
    private final ConsentType consentType;

    @NotNull(message = "Consent timestamp is required")
    private final LocalDateTime consentTimestamp;

    private EkycInitiateRequest(Builder builder) {
        this.idNumber = builder.idNumber;
        this.idType = builder.idType;
        this.mobileNumber = builder.mobileNumber;
        this.consentType = builder.consentType;
        this.consentTimestamp = builder.consentTimestamp;
    }

    // Getters only - immutable
    public String getIdNumber() { return idNumber; }
    public IdType getIdType() { return idType; }
    public String getMobileNumber() { return mobileNumber; }
    public ConsentType getConsentType() { return consentType; }
    public LocalDateTime getConsentTimestamp() { return consentTimestamp; }

    public static class Builder {
        private String idNumber;
        private IdType idType;
        private String mobileNumber;
        private ConsentType consentType;
        private LocalDateTime consentTimestamp;

        public Builder idNumber(String idNumber) {
            this.idNumber = idNumber;
            return this;
        }

        public Builder idType(IdType idType) {
            this.idType = idType;
            return this;
        }

        public Builder mobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
            return this;
        }

        public Builder consentType(ConsentType consentType) {
            this.consentType = consentType;
            return this;
        }

        public Builder consentTimestamp(LocalDateTime consentTimestamp) {
            this.consentTimestamp = consentTimestamp;
            return this;
        }

        public EkycInitiateRequest build() {
            return new EkycInitiateRequest(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EkycInitiateRequest)) return false;
        EkycInitiateRequest that = (EkycInitiateRequest) o;
        return Objects.equals(idNumber, that.idNumber) &&
                idType == that.idType &&
                Objects.equals(mobileNumber, that.mobileNumber) &&
                consentType == that.consentType &&
                Objects.equals(consentTimestamp, that.consentTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNumber, idType, mobileNumber, consentType, consentTimestamp);
    }
}
```