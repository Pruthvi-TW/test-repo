package com.ekyc.dto.request;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.ekyc.enums.ConsentType;
import com.ekyc.enums.IdType;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Request DTO for initiating the eKYC process.
 * Contains validated fields for identity verification.
 */
public class EkycInitiateRequest {

    @NotBlank(message = "Customer ID is required")
    @Size(min = 5, max = 50, message = "Customer ID must be between 5 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Customer ID can only contain alphanumeric characters, hyphens, and underscores")
    private String customerId;

    @NotNull(message = "ID type is required")
    private IdType idType;

    @NotBlank(message = "ID number is required")
    @Size(min = 8, max = 16, message = "ID number must be between 8 and 16 characters")
    private String idNumber;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be a valid 10-digit Indian mobile number")
    private String mobileNumber;

    @NotNull(message = "Consent is required")
    private ConsentType consentType;

    @NotNull(message = "Consent timestamp is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?$", 
             message = "Consent timestamp must be in ISO-8601 format (yyyy-MM-dd'T'HH:mm:ss.SSS)")
    private String consentTimestamp;

    @Size(max = 100, message = "Callback URL must not exceed 100 characters")
    @Pattern(regexp = "^(https?://)[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,}(:[0-9]+)?(/[a-zA-Z0-9\\-\\._~:/?#\\[\\]@!$&'()*+,;=]*)?$", 
             message = "Callback URL must be a valid URL")
    private String callbackUrl;

    @Size(max = 500, message = "Additional data must not exceed 500 characters")
    private String additionalData;

    // Default constructor
    public EkycInitiateRequest() {
    }

    // All-args constructor
    public EkycInitiateRequest(String customerId, IdType idType, String idNumber, String mobileNumber,
                              ConsentType consentType, String consentTimestamp, String callbackUrl,
                              String additionalData) {
        this.customerId = customerId;
        this.idType = idType;
        this.idNumber = idNumber;
        this.mobileNumber = mobileNumber;
        this.consentType = consentType;
        this.consentTimestamp = consentTimestamp;
        this.callbackUrl = callbackUrl;
        this.additionalData = additionalData;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public ConsentType getConsentType() {
        return consentType;
    }

    public void setConsentType(ConsentType consentType) {
        this.consentType = consentType;
    }

    public String getConsentTimestamp() {
        return consentTimestamp;
    }

    public void setConsentTimestamp(String consentTimestamp) {
        this.consentTimestamp = consentTimestamp;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    /**
     * Returns a masked version of the ID number for logging purposes
     * @return masked ID number
     */
    @JsonIgnore
    public String getMaskedIdNumber() {
        if (idNumber == null || idNumber.length() < 8) {
            return "INVALID_ID";
        }
        
        int length = idNumber.length();
        int visibleChars = Math.min(4, length / 4);
        
        return "X".repeat(length - visibleChars) + idNumber.substring(length - visibleChars);
    }

    /**
     * Returns a masked version of the mobile number for logging purposes
     * @return masked mobile number
     */
    @JsonIgnore
    public String getMaskedMobileNumber() {
        if (mobileNumber == null || mobileNumber.length() < 10) {
            return "INVALID_MOBILE";
        }
        
        return "XXXXXX" + mobileNumber.substring(mobileNumber.length() - 4);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EkycInitiateRequest that = (EkycInitiateRequest) o;
        return Objects.equals(customerId, that.customerId) &&
                idType == that.idType &&
                Objects.equals(idNumber, that.idNumber) &&
                Objects.equals(mobileNumber, that.mobileNumber) &&
                consentType == that.consentType &&
                Objects.equals(consentTimestamp, that.consentTimestamp) &&
                Objects.equals(callbackUrl, that.callbackUrl) &&
                Objects.equals(additionalData, that.additionalData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, idType, idNumber, mobileNumber, consentType, consentTimestamp, callbackUrl, additionalData);
    }

    @Override
    public String toString() {
        return "EkycInitiateRequest{" +
                "customerId='" + customerId + '\'' +
                ", idType=" + idType +
                ", idNumber='" + getMaskedIdNumber() + '\'' +
                ", mobileNumber='" + getMaskedMobileNumber() + '\'' +
                ", consentType=" + consentType +
                ", consentTimestamp='" + consentTimestamp + '\'' +
                ", callbackUrl='" + callbackUrl + '\'' +
                ", additionalData='" + (additionalData != null ? "PRESENT" : "NULL") + '\'' +
                '}';
    }

    /**
     * Builder class for EkycInitiateRequest
     */
    public static class Builder {
        private String customerId;
        private IdType idType;
        private String idNumber;
        private String mobileNumber;
        private ConsentType consentType;
        private String consentTimestamp;
        private String callbackUrl;
        private String additionalData;

        public Builder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder idType(IdType idType) {
            this.idType = idType;
            return this;
        }

        public Builder idNumber(String idNumber) {
            this.idNumber = idNumber;
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

        public Builder consentTimestamp(String consentTimestamp) {
            this.consentTimestamp = consentTimestamp;
            return this;
        }

        public Builder callbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public Builder additionalData(String additionalData) {
            this.additionalData = additionalData;
            return this;
        }

        public EkycInitiateRequest build() {
            return new EkycInitiateRequest(
                    customerId,
                    idType,
                    idNumber,
                    mobileNumber,
                    consentType,
                    consentTimestamp,
                    callbackUrl,
                    additionalData
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}